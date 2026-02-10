# LORM Usage Guide

This README provides a quick start for basic usage, relations (hasMany / belongsTo), eager loading, and queries, based on the code in this repository.

## 1) Basic Setup

Create a model class that extends `Lorm<T>` and annotate its columns.

```java
import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.annot.columns.Column;

@Table("users")
public class User extends Lorm<User> {
    @Column(value = "id", primaryKey = true)
    private Integer id;

    @Column("name")
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

Create a connection (example using `DatabaseConnection`).

```java
import mg.razherana.database.DatabaseConnection;

DatabaseConnection db = DatabaseConnection.fromDotEnv("db.xml");
try (java.sql.Connection connection = db.getConnection()) {
    User user = new User().find(1, connection);
    System.out.println(user.getName());
}
```

## 2) Relations

Relations are declared at the class level with annotations.

### 2.1) hasMany

`@HasMany` is declared on the parent model class. The child model must have a foreign key field annotated with `@ForeignColumn`.

```java
import mg.razherana.lorm.annot.relations.HasMany;

@HasMany(model = Post.class, foreignKey = "user_id", relationName = "posts")
@Table("users")
public class User extends Lorm<User> {
    @Column(value = "id", primaryKey = true)
    private Integer id;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public List<Post> posts(java.sql.Connection connection) throws java.sql.SQLException {
        return hasMany("posts", connection);
    }
}
```

Child model with the foreign key:

```java
import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.annot.general.Table;

@Table("posts")
public class Post extends Lorm<Post> {
    @Column(value = "id", primaryKey = true)
    private Integer id;

    @Column("user_id")
    @ForeignColumn(model = User.class, name = "id")
    private Integer userId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
```

### 2.2) belongsTo

`@BelongsTo` is declared on the child model class, pointing to the parent model and the same foreign key column.

```java
import mg.razherana.lorm.annot.relations.BelongsTo;

@BelongsTo(model = User.class, foreignKey = "user_id", relationName = "user")
@Table("posts")
public class Post extends Lorm<Post> {
    @Column(value = "id", primaryKey = true)
    private Integer id;

    @Column("user_id")
    @ForeignColumn(model = User.class, name = "id")
    private Integer userId;

    public User user(java.sql.Connection connection) throws java.sql.SQLException {
        return belongsTo("user", connection);
    }
}
```

## 3) Eager Loading

You can declare eager loads on the model class:

```java
import mg.razherana.lorm.annot.relations.EagerLoad;

@EagerLoad({"posts"})
@Table("users")
public class User extends Lorm<User> {
    @Column(value = "id", primaryKey = true)
    private Integer id;
}
```

Or add them programmatically before querying:

```java
User userModel = new User().addEagerLoad("posts");
List<User> users = userModel.all(connection);
```

Nested eager loading is supported:

```java
User userModel = new User();
userModel.addNestedEagerLoad("posts").addEagerLoad("comments");
List<User> users = userModel.all(connection);
```

## 4) Basic Queries

Build `where` clauses with `WhereBuilder`.

```java
import mg.razherana.lorm.queries.WhereBuilder;

List<mg.razherana.lorm.queries.WhereContainer> where =
        WhereBuilder.create()
                .andWhere("status", "active", "=")
                .andWhere("id", 10, ">")
                .then();

List<User> users = new User().where(where, connection);
```

Add extra SQL (order/limit) if needed:

```java
List<User> users = new User().where("ORDER BY id DESC LIMIT 10", where, connection);
```

Find by primary key (field annotated with `@Column(primaryKey = true)`):

```java
User user = new User().find(1, connection);
```

## 5) Notes

- All queries require a `java.sql.Connection`.
- Relations rely on `@ForeignColumn` in the child model.
- Eager-load relation names must match the relation name in the annotations (for example, `posts`, `user`).
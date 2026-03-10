package mg.razherana.lorm.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhereContainer {
  final private String element1;
  final private Object element2;
  final private String operator;
  final private String next;
  final private boolean intoParameters;

  public boolean isIntoParameters() {
    return intoParameters;
  }

  public WhereContainer(String element1, Object element2, String operator) {
    this(element1, element2, operator, "AND", true);
  }

  public WhereContainer(String element1, Object element2, String operator, boolean intoParameters) {
    this(element1, element2, operator, "AND", intoParameters);
  }

  public WhereContainer(String element1, Object element2, String operator, String next) {
    this(element1, element2, operator, next, true);
  }

  public WhereContainer(String element1, Object element2, String operator, String next, boolean intoParameters) {
    this.element1 = element1;
    this.element2 = element2;
    this.operator = operator;
    this.next = next;
    this.intoParameters = intoParameters;
  }

  public static Map.Entry<String, Object[]> toConditionClause(List<WhereContainer> whereContainers) {
    if (whereContainers == null || whereContainers.isEmpty())
      return Map.entry("", new Object[] {});

    StringBuilder where = new StringBuilder();
    ArrayList<Object> queryParams = new ArrayList<>();

    for (int i = 0; i < whereContainers.size(); i++) {
      WhereContainer whereContainer = whereContainers.get(i);

      // Build the condition
      where.append(whereContainer.getElement1())
          .append(" ")
          .append(whereContainer.getOperator())
          .append(" ");

      if (whereContainer.isIntoParameters()) {
        where.append("?");
        queryParams.add(whereContainer.getElement2());
      } else {
        where.append(whereContainer.getElement2());
      }

      // Add the "next" operator if this is not the last condition
      if (i < whereContainers.size() - 1) {
        where.append(" ")
            .append(whereContainer.getNext())
            .append(" ");
      }
    }

    return Map.entry(where.toString(), queryParams.toArray(Object[]::new));
  }

  public String getNext() {
    return next;
  }

  public String getElement1() {
    return element1;
  }

  public Object getElement2() {
    return element2;
  }

  public String getOperator() {
    return operator;
  }

}

/* Generated by JTB 1.4.4 */
package syntaxtree;

import visitor.*;

public class insertar implements INode_usql {

  public NodeToken f0;

  public NodeToken f1;

  public NodeToken f2;

  public NodeToken f3;

  public NodeToken f4;

  public NodeChoice f5;

  public NodeToken f6;

  private static final long serialVersionUID = 144L;

  public insertar(final NodeToken n0, final NodeToken n1, final NodeToken n2, final NodeToken n3, final NodeToken n4, final NodeChoice n5, final NodeToken n6) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
  }

  public insertar(final NodeToken n0, final NodeChoice n1) {
    f0 = new NodeToken("insertar");
    f1 = new NodeToken("en");
    f2 = new NodeToken("tabla");
    f3 = n0;
    f4 = new NodeToken("(");
    f5 = n1;
    f6 = new NodeToken(";");
  }

  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}

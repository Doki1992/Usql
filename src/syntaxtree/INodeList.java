/* Generated by JTB 1.4.4 */
package syntaxtree;

import visitor.IRetArguVisitor;
import visitor.IRetVisitor;
import visitor.IVoidArguVisitor;
import visitor.IVoidVisitor;

public interface INodeList extends INode_usql {

  public void addNode(final INode_usql n);

  public INode_usql elementAt(int i);

  public java.util.Iterator<INode_usql> elements();

  public int size();

  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu);

  public <R> R accept(final IRetVisitor<R> vis);

  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu);

  public void accept(final IVoidVisitor vis);

}

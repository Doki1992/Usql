/* Generated by JTB 1.4.4 */
package visitorxml;

import arbolxml.*;
import java.util.*;

public class DepthFirstRetArguVisitor<R, A> implements IRetArguVisitor<R, A> {


  public R visit(final NodeChoice n, final A argu) {
    final R nRes = n.choice.accept(this, argu);
    return nRes;
  }

  public R visit(final NodeList n, final A argu) {
    R nRes = null;
    for (final Iterator<INode> e = n.elements(); e.hasNext();) {
      @SuppressWarnings("unused")
      final R sRes = e.next().accept(this, argu);
    }
    return nRes;
  }

  public R visit(final NodeListOptional n, final A argu) {
    if (n.present()) {
      R nRes = null;
      for (final Iterator<INode> e = n.elements(); e.hasNext();) {
        @SuppressWarnings("unused")
        R sRes = e.next().accept(this, argu);
        }
      return nRes;
    } else
      return null;
  }

  public R visit(final NodeOptional n, final A argu) {
    if (n.present()) {
      final R nRes = n.node.accept(this, argu);
      return nRes;
    } else
    return null;
  }

  public R visit(final NodeSequence n, final A argu) {
    R nRes = null;
    for (final Iterator<INode> e = n.elements(); e.hasNext();) {
      @SuppressWarnings("unused")
      R subRet = e.next().accept(this, argu);
    }
    return nRes;
  }

  public R visit(final NodeToken n, @SuppressWarnings("unused") final A argu) {
    R nRes = null;
    @SuppressWarnings("unused")
    final String tkIm = n.tokenImage;
    return nRes;
  }

  public R visit(final Inicio n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    return nRes;
  }

  public R visit(final lista_xml n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    return nRes;
  }

  public R visit(final usuarioxml n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    n.f6.accept(this, argu);
    n.f7.accept(this, argu);
    n.f8.accept(this, argu);
    n.f9.accept(this, argu);
    n.f10.accept(this, argu);
    return nRes;
  }

  public R visit(final procedimiento n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    n.f6.accept(this, argu);
    n.f7.accept(this, argu);
    n.f8.accept(this, argu);
    n.f9.accept(this, argu);
    n.f10.accept(this, argu);
    n.f11.accept(this, argu);
    n.f12.accept(this, argu);
    n.f13.accept(this, argu);
    n.f14.accept(this, argu);
    n.f15.accept(this, argu);
    n.f16.accept(this, argu);
    return nRes;
  }

  public R visit(final proc n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    return nRes;
  }

  public R visit(final objeto n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    n.f6.accept(this, argu);
    n.f7.accept(this, argu);
    n.f8.accept(this, argu);
    n.f9.accept(this, argu);
    n.f10.accept(this, argu);
    n.f11.accept(this, argu);
    n.f12.accept(this, argu);
    n.f13.accept(this, argu);
    return nRes;
  }

  public R visit(final obj n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    return nRes;
  }

  public R visit(final Db n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    return nRes;
  }

  public R visit(final lista_db n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    return nRes;
  }

  public R visit(final Tablaxml n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    n.f6.accept(this, argu);
    n.f7.accept(this, argu);
    n.f8.accept(this, argu);
    n.f9.accept(this, argu);
    n.f10.accept(this, argu);
    n.f11.accept(this, argu);
    n.f12.accept(this, argu);
    n.f13.accept(this, argu);
    n.f14.accept(this, argu);
    n.f15.accept(this, argu);
    n.f16.accept(this, argu);
    return nRes;
  }

  public R visit(final lista_row n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    return nRes;
  }

  public R visit(final atributo n, final A argu) {
    R nRes = null;
    n.f0.accept(this, argu);
    return nRes;
  }

}
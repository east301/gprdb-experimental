/**
 * A "yield return" implementation for Java
 * By Jim Blackler (jimblackler@gmail.com)
 *
 * http://jimblackler.net/blog/?p=61
 * http://svn.jimblackler.net/jimblackler/trunk/IdeaProjects/YieldAdapter/
 */
package com.zoominfo.util.yieldreturn;

/**
 * A class to convert methods that implement the Collector&lt;&gt; class into a standard Iterable&lt;&gt;.
 */
public interface YieldAdapter<T> {

    YieldAdapterIterable<T> adapt(Collector<T> client);
}

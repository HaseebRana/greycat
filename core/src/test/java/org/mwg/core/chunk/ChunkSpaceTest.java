package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.core.CoreConstants;
import org.mwg.core.chunk.heap.HeapChunkSpace;
import org.mwg.core.chunk.offheap.*;
import org.mwg.core.utility.Unsafe;

public class ChunkSpaceTest {
    @Test
    public void heapChunkSpaceTest() {
        test(new HeapChunkSpace(10, 10));
    }

    @Test
    public void offHeapChunkSpaceTest() {
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        OffHeapChunkSpace space = new OffHeapChunkSpace(10, 10);
        test(space);

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
    }


    public void test(ChunkSpace space) {
        StateChunk stateChunk = (StateChunk) space.create(CoreConstants.STATE_CHUNK, 0, 0, 0, null, null);
        space.putAndMark(stateChunk);

        WorldOrderChunk worldOrderChunk = (WorldOrderChunk) space.create(CoreConstants.WORLD_ORDER_CHUNK, 0, 0, 1, null, null);
        space.putAndMark(worldOrderChunk);

        TimeTreeChunk timeTreeChunk = (TimeTreeChunk) space.create(CoreConstants.TIME_TREE_CHUNK, 0, 0, 2, null, null);
        space.putAndMark(timeTreeChunk);

        space.free();
    }
}

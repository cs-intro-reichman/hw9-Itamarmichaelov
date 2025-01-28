/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {
	
	// A list of the memory blocks that are presently allocated
	private LinkedList allocatedList;

	// A list of memory blocks that are presently free
	private LinkedList freeList;

	/**
	 * Constructs a new managed memory space of a given maximal size.
	 * 
	 * @param maxSize
	 *            the size of the memory space to be managed
	 */
	public MemorySpace(int maxSize) {
		// initiallizes an empty list of allocated blocks.
		allocatedList = new LinkedList();
	    // Initializes a free list containing a single block which represents
	    // the entire memory. The base address of this single initial block is
	    // zero, and its length is the given memory size.
		freeList = new LinkedList();
		freeList.addLast(new MemoryBlock(0, maxSize));
	}

	/**
	 * Allocates a memory block of a requested length (in words). Returns the
	 * base address of the allocated block, or -1 if unable to allocate.
	 * 
	 * This implementation scans the freeList, looking for the first free memory block 
	 * whose length equals at least the given length. If such a block is found, the method 
	 * performs the following operations:
	 * 
	 * (1) A new memory block is constructed. The base address of the new block is set to
	 * the base address of the found free block. The length of the new block is set to the value 
	 * of the method's length parameter.
	 * 
	 * (2) The new memory block is appended to the end of the allocatedList.
	 * 
	 * (3) The base address and the length of the found free block are updated, to reflect the allocation.
	 * For example, suppose that the requested block length is 17, and suppose that the base
	 * address and length of the the found free block are 250 and 20, respectively.
	 * In such a case, the base address and length of of the allocated block
	 * are set to 250 and 17, respectively, and the base address and length
	 * of the found free block are set to 267 and 3, respectively.
	 * 
	 * (4) The new memory block is returned.
	 * 
	 * If the length of the found block is exactly the same as the requested length, 
	 * then the found block is removed from the freeList and appended to the allocatedList.
	 * 
	 * @param length
	 *        the length (in words) of the memory block that has to be allocated
	 * @return the base address of the allocated block, or -1 if unable to allocate
	 */
	public int malloc(int length) {
		ListIterator iterator = freeList.iterator(); // Create an iterator for the free list
		Node previous = null; // Keep track of the previous node
	
		while (iterator.hasNext()) {
			Node current = iterator.current; // Current node
			MemoryBlock freeBlock = iterator.next(); // Access the memory block
	
			if (freeBlock.length >= length) {
				// Create a new allocated block
				MemoryBlock allocatedBlock = new MemoryBlock(freeBlock.baseAddress, length);
				allocatedList.addLast(allocatedBlock);
	
				// Update or remove the free block
				freeBlock.baseAddress += length;
				freeBlock.length -= length;
				if (freeBlock.length == 0) {
					if (previous == null) {
						freeList.getFirst().next = current.next; // Update head if needed
					} else {
						previous.next = current.next; // Bypass the current node
					}
					freeList.remove(current.block); // Remove fully used block
				}
	
				return allocatedBlock.baseAddress; // Return the allocated base address
			}
	
			// Update previous node and continue iteration
			previous = current;
		}
	
		return -1; // Return -1 if no suitable block is found
	}

	
	
	

	/**
	 * Frees the memory block whose base address equals the given address.
	 * This implementation deletes the block whose base address equals the given 
	 * address from the allocatedList, and adds it at the end of the free list. 
	 * 
	 * @param baseAddress
	 *            the starting address of the block to freeList
	 */
	public void free(int address) {
		Node current = allocatedList.getFirst(); // Start at the first node in the allocated list
		while (current != null) {
			MemoryBlock block = current.block; // Access the block from the node
			if (block.baseAddress == address) { // Check if the base address matches
				allocatedList.remove(current); // Remove the block from the allocated list
				freeList.addLast(block); // Add it to the free list
				return; // Exit after freeing the block
			}
			current = current.next; // Move to the next node
		}
	}
	
	/**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
	public String toString() {
		return freeList.toString() + "\n" + allocatedList.toString();
	}
	
	/**
	 * Performs defragmentation of this memory space.
	 * Normally, called by malloc, when it fails to find a memory block of the requested size.
	 */
	public void defrag() {
		boolean swapped;
		do {
			swapped = false;
			Node current = freeList.getFirst();
			while (current != null && current.next != null) {
				MemoryBlock currentBlock = current.block;
				MemoryBlock nextBlock = current.next.block;
				if (currentBlock.baseAddress > nextBlock.baseAddress) {
					MemoryBlock temp = current.block;
					current.block = nextBlock;
					current.next.block = temp;
					swapped = true;
				}
				current = current.next;
			}
		} while (swapped);

		Node current = freeList.getFirst();
		while (current != null && current.next != null) {
			MemoryBlock currentBlock = current.block;
			MemoryBlock nextBlock = current.next.block;
			if (currentBlock.baseAddress + currentBlock.length == nextBlock.baseAddress) {
				currentBlock.length += nextBlock.length;
				freeList.remove(current.next);
			} else {
				current = current.next;
			}
		}
	}
}

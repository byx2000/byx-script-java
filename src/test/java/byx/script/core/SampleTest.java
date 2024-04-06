package byx.script.core;

import org.junit.jupiter.api.Test;

import static byx.script.core.TestUtils.verify;

public class SampleTest {
    @Test
    public void testBinaryTreeTraverse() {
        verify("""
                // 二叉树定义
                func TreeNode(val, left, right) {
                    return {val, left, right}
                }
                                
                // 前序遍历
                func preorderTraverse(root) {
                    var result = []
                    func doTraverse(root) {
                        if (root == null) {
                            return
                        }
                        result.addLast(root.val)
                        doTraverse(root.left)
                        doTraverse(root.right)
                    }
                    doTraverse(root)
                    return result
                }
                                
                // 中序遍历
                func inorderTraverse(root) {
                    var result = []
                    func doTraverse(root) {
                        if (root == null) {
                            return
                        }
                        doTraverse(root.left)
                        result.addLast(root.val)
                        doTraverse(root.right)
                    }
                    doTraverse(root)
                    return result
                }
                                
                // 后序遍历
                func postorderTraverse(root) {
                    var result = []
                    func doTraverse(root) {
                        if (root == null) {
                            return
                        }
                        doTraverse(root.left)
                        doTraverse(root.right)
                        result.addLast(root.val)
                    }
                    doTraverse(root)
                    return result
                }
                                
                // 层序遍历
                func levelTraverse(root) {
                    var result = []
                    var queue = [root]
                    while (!queue.isEmpty()) {
                        var cnt = queue.length()
                        for (var i = 0; i < cnt; ++i) {
                            var n = queue.removeFirst()
                            result.addLast(n.val)
                            if (n.left != null) {
                                queue.addLast(n.left)
                            }
                            if (n.right != null) {
                                queue.addLast(n.right)
                            }
                        }
                    }
                    return result
                }
                                
                var root = TreeNode(1, TreeNode(2, TreeNode(4, TreeNode(7), null), TreeNode(5)), TreeNode(3, null, TreeNode(6)))
                                
                Console.println(preorderTraverse(root))
                Console.println(inorderTraverse(root))
                Console.println(postorderTraverse(root))
                Console.println(levelTraverse(root))
                """, """
                [1, 2, 4, 7, 5, 3, 6]
                [7, 4, 2, 5, 1, 3, 6]
                [7, 4, 5, 2, 6, 3, 1]
                [1, 2, 3, 4, 5, 6, 7]
                """);
    }

    @Test
    public void testCounter() {
        verify("""
                // 简单计数器
                func SimpleCounter() {
                    var cnt = 0
                    return () => {
                        cnt = cnt + 1
                        return cnt
                    }
                }
                                
                var c1 = SimpleCounter()
                Console.println(c1())
                Console.println(c1())
                Console.println(c1())
                                
                var c2 = SimpleCounter()
                Console.println(c2())
                Console.println(c2())
                Console.println(c2())
                                
                Console.println(c1())
                Console.println(c2())
                                
                // 计数器
                func Counter(init) {
                    var cnt = init
                    return {
                        // 获取当前计数值
                        current: () => cnt,
                        // 计数值+1
                        inc: () => {cnt++},
                        // 计数值-1
                        dec: () => {cnt--}
                    }
                }
                                
                var c3 = Counter(100)
                Console.println(c3.current()) // 100
                c3.inc()
                Console.println(c3.current()) // 101
                c3.inc()
                Console.println(c3.current()) // 102
                c3.dec()
                Console.println(c3.current()) // 101
                c3.dec()
                Console.println(c3.current()) // 100
                                
                var c4 = Counter(200)
                Console.println(c4.current()) // 200
                c4.inc()
                Console.println(c4.current()) // 201
                c4.inc()
                Console.println(c4.current()) // 202
                c4.dec()
                Console.println(c4.current()) // 201
                c4.dec()
                Console.println(c4.current()) // 200
                """, """
                1
                2
                3
                1
                2
                3
                4
                4
                100
                101
                102
                101
                100
                200
                201
                202
                201
                200
                """);
    }

    @Test
    public void testFactorial() {
        verify("""
                func factorial(n) {
                    if (n == 1) {
                        return 1
                    }
                    return n * factorial(n - 1)
                }
                                
                Console.println(factorial(10))
                """, """
                3628800
                """);
    }

    @Test
    public void testFibonacci() {
        verify("""
                var fib1 = n => {
                    if (n == 1 || n == 2) {
                        return 1
                    }
                    return fib1(n - 1) + fib1(n - 2)
                }
                                
                func fib2(n) {
                    if (n == 1 || n == 2) {
                        return 1
                    }
                    return fib2(n - 1) + fib2(n - 2)
                }
                                
                Console.println(fib1(10))
                Console.println(fib1(20))
                Console.println(fib2(10))
                Console.println(fib2(20))
                """, """
                55
                6765
                55
                6765
                """);
    }

    @Test
    public void testMatrixTranspose() {
        verify("""
                func transpose(matrix) {
                    for (var i = 0; i < matrix.length(); i = i + 1) {
                        for (var j = i; j < matrix.length(); j = j + 1) {
                            var t = matrix[i][j]
                            matrix[i][j] = matrix[j][i]
                            matrix[j][i] = t
                        }
                    }
                }
                                
                var mat = [
                    [1, 2, 3],
                    [4, 5, 6],
                    [7, 8, 9]
                ]
                transpose(mat)
                                
                for (var i = 0; i < mat.length(); ++i) {
                    Console.println(mat[i])
                }
                """, """
                [1, 4, 7]
                [2, 5, 8]
                [3, 6, 9]
                """);
    }

    @Test
    public void testMergeSort() {
        verify("""
                func MergeSort(nums) {
                    var temp = nums.copy()

                    func merge(left, mid, right) {
                        var i = left
                        var j = mid + 1
                       
                        for (var k = left; k <= right; k++) {
                            if (i > mid) {
                                temp[k] = nums[j]
                                j = j + 1
                            } else if (j > right) {
                                temp[k] = nums[i]
                                i = i + 1
                            } else if (nums[i] < nums[j]) {
                                temp[k] = nums[i]
                                i = i + 1
                            } else {
                                temp[k] = nums[j]
                                j = j + 1
                            }
                        }
                       
                        for (var k = left; k <= right; ++k) {
                            nums[k] = temp[k]
                        }
                    }
                   
                    func sort(left, right) {
                        if (left >= right) {
                            return
                        }
                        var mid = left + (right - left) / 2
                        sort(left, mid);
                        sort(mid + 1, right)
                        merge(left, mid, right)
                    }
                   
                    sort(0, nums.length() - 1)
                }
                   
                var nums = [3, 2, 1, 5, 4, 6, 9, 7, 8]
                MergeSort(nums)
                Console.println(nums)
                """, """
                [1, 2, 3, 4, 5, 6, 7, 8, 9]
                """);
    }

    @Test
    public void testNumberOfIsland() {
        verify("""
                func numberOfIslands(map) {
                    func dfs(r, c) {
                        if (r < 0 || r >= map.length() || c < 0 || c >= map[r].length()) {
                            return
                        }
                        if (map[r][c] == '0') {
                            return
                        }
                        map[r][c] = '0'
                        dfs(r - 1, c)
                        dfs(r + 1, c)
                        dfs(r, c - 1)
                        dfs(r, c + 1)
                    }
                   
                    var cnt = 0
                    for (var i = 0; i < map.length(); ++i) {
                        for (var j = 0; j < map[i].length(); ++j) {
                            if (map[i][j] == '1') {
                                cnt++
                                dfs(i, j)
                            }
                        }
                    }
                    return cnt
                }
                                
                var map1 = [
                    ['1', '1', '1', '1', '0'],
                    ['1', '1', '0', '1', '0'],
                    ['1', '1', '0', '0', '0'],
                    ['0', '0', '0', '0', '0']
                ]
                Console.println(numberOfIslands(map1))
                                
                var map2 = [
                    ['1', '1', '0', '0', '0'],
                    ['1', '1', '0', '0', '0'],
                    ['0', '0', '1', '0', '0'],
                    ['0', '0', '0', '1', '1']
                ]
                Console.println(numberOfIslands(map2))
                """, """
                1
                3
                """);
    }

    @Test
    public void testPalindrome() {
        verify("""
                func LongestPalindrome1(s) {
                    func isPalindrome(i, j) {
                        if (i == j) {
                            return true
                        } else if (i + 1 == j) {
                            return s.charAt(i) == s.charAt(j)
                        } else if (s.charAt(i) == s.charAt(j)) {
                            return isPalindrome(i + 1, j - 1)
                        } else {
                            return false
                        }
                    }
                 
                    var maxLen = -1
                    for (var i = 0; i < s.length(); i++) {
                        for (var j = i; j < s.length(); j++) {
                            if (isPalindrome(i, j) && j - i + 1 > maxLen) {
                                maxLen = j - i + 1
                            }
                        }
                    }
                                
                    return maxLen
                }
                   
                Console.println(LongestPalindrome1('babad'))
                Console.println(LongestPalindrome1('cbbd'))
                Console.println(LongestPalindrome1('dsfasfdsfadsfasdgfdadfasdgdsgasdfgasgsfd'))
                Console.println(LongestPalindrome1('asdasbvabsbasbasdabbabaabsdsbaabaabbababaddsbfbaba'))
                                
                func LongestPalindrome2(s) {
                    var cache = []
                    for (var i = 0; i < s.length(); ++i) {
                        cache.addLast([])
                        for (var j = 0; j < s.length(); ++j) {
                            cache[i].addLast(null)
                        }
                    }
                     
                    func isPalindrome(i, j) {
                        if (cache[i][j] != null) {
                            return cache[i][j]
                        }
                 
                        if (i == j) {
                            cache[i][j] = true
                        } else if (i + 1 == j) {
                            cache[i][j] = s.charAt(i) == s.charAt(j)
                        } else if (s.charAt(i) == s.charAt(j)) {
                            cache[i][j] = isPalindrome(i + 1, j - 1)
                        } else {
                            cache[i][j] = false
                        }
                 
                        return cache[i][j]
                    }
                 
                    var maxLen = -1
                    for (var i = 0; i < s.length(); ++i) {
                        for (var j = i; j < s.length(); ++j) {
                            if (isPalindrome(i, j) && j - i + 1 > maxLen) {
                                maxLen = j - i + 1
                            }
                        }
                    }
                                
                    return maxLen
                }
                
                Console.println(LongestPalindrome2('babad'))
                Console.println(LongestPalindrome2('cbbd'))
                Console.println(LongestPalindrome2('dsfasfdsfadsfasdgfdadfasdgdsgasdfgasgsfd'))
                Console.println(LongestPalindrome2('asdasbvabsbasbasdabbabaabsdsbaabaabbababaddsbfbaba'))
                """, """
                3
                2
                5
                13
                3
                2
                5
                13
                """);
    }

    @Test
    public void testPermutation() {
        verify("""
                func permutation(nums) {
                    var result = []
                               
                    var book = []
                    for (var i = 0; i < nums.length(); i++) {
                        book.addLast(false)
                    }
                               
                    var p = []
                    func dfs(index) {
                        if (index == nums.length()) {
                            result.addLast(p.copy())
                            return
                        }
                               
                        for (var i = 0; i < nums.length(); ++i) {
                            if (!book[i]) {
                                book[i] = true
                                p.addLast(nums[i])
                                dfs(index + 1)
                                p.removeLast()
                                book[i] = false
                            }
                        }
                    }
                               
                    dfs(0)
                               
                    return result
                }
                                
                var result = permutation([1, 2, 3])
                for (var i = 0; i < result.length(); ++i) {
                    Console.println(result[i])
                }
                """, """
                [1, 2, 3]
                [1, 3, 2]
                [2, 1, 3]
                [2, 3, 1]
                [3, 1, 2]
                [3, 2, 1]
                """);
    }

    @Test
    public void testQuickSort() {
        verify("""
                func swap(nums, i, j) {
                    var t = nums[i]
                    nums[i] = nums[j]
                    nums[j] = t
                }
                                
                func partition(nums, left, right) {
                    var i = left + 1
                    var j = right
                    while (i <= j) {
                        while (i <= j && nums[i] <= nums[left]) {++i}
                        while (i <= j && nums[j] > nums[left]) {--j}
                        if (i < j) {
                            swap(nums, i, j)
                        }
                    }
                    swap(nums, left, j)
                    return j
                }
                                
                func qsort(nums, left, right) {
                    if (left >= right) {
                        return
                    }
                    var mid = partition(nums, left, right)
                    qsort(nums, left, mid - 1)
                    qsort(nums, mid + 1, right)
                }
                                
                var nums1 = [3, 2, 1, 5, 4, 6, 9, 7, 8]
                qsort(nums1, 0, nums1.length() - 1)
                Console.println(nums1)
                                
                var nums2 = [2, 4, 3, 2, 5, 7]
                qsort(nums2, 0, nums2.length() - 1)
                Console.println(nums2)
                                
                """, """
                [1, 2, 3, 4, 5, 6, 7, 8, 9]
                [2, 2, 3, 4, 5, 7]
                """);
    }

    @Test
    public void testReverseLinkedList() {
        verify("""
                // 链表定义
                func LinkedList(val, next) {
                    return {
                        val: val,
                        next: next
                    }
                }
                                
                // 将链表转换为list
                func toList(head) {
                    var result = []
                    func traverse(head) {
                        if (head == null) {
                            return
                        }
                        result.addLast(head.val)
                        traverse(head.next)
                    }
                    traverse(head)
                    return result
                }
                                
                // 反转链表
                func reverse(head) {
                    if (head == null || head.next == null) {
                        return head
                    }
                    var p = head.next
                    var q = reverse(head.next)
                    p.next = head
                    head.next = null
                    return q
                }
                                
                var head = LinkedList(1, LinkedList(2, LinkedList(3, LinkedList(4, LinkedList(5)))))
                Console.println(toList(head))
                Console.println(toList(reverse(head)))
                """, """
                [1, 2, 3, 4, 5]
                [5, 4, 3, 2, 1]
                """);
    }

    @Test
    public void testReverseList() {
        verify("""
                func reverse(nums) {
                    var i = 0;
                    var j = nums.length() - 1;
                    while (i < j) {
                        var t = nums[i]
                        nums[i] = nums[j]
                        nums[j] = t
                        i++
                        j--
                    }
                }
                                
                var nums1 = [1, 2, 3, 4, 5]
                reverse(nums1)
                Console.println(nums1)
                var nums2 = [1, 2, 3, 4, 5, 6]
                reverse(nums2)
                Console.println(nums2)
                """, """
                [5, 4, 3, 2, 1]
                [6, 5, 4, 3, 2, 1]
                """);
    }

    @Test
    public void testSelectionSort() {
        verify("""
                func SelectionSort(nums) {
                    for (var i = 0; i < nums.length(); i = i + 1) {
                        for (var j = i + 1; j < nums.length(); j = j + 1) {
                            if (nums[j] < nums[i]) {
                                var t = nums[i]
                                nums[i] = nums[j]
                                nums[j] = t
                            }
                        }
                    }
                }
                                
                var nums = [3, 2, 1, 5, 4, 6, 9, 7, 8]
                SelectionSort(nums)
                                
                for (var i = 0; i < nums.length(); ++i) {
                    Console.print(nums[i] + ' ')
                }
                """, """
                1 2 3 4 5 6 7 8 9
                """);
    }

    @Test
    public void testStack() {
        verify("""
                func Stack() {
                    var elems = []
                    return {
                        push: e => elems.addLast(e),
                        pop: () => elems.removeLast(),
                        top: () => elems[elems.length() - 1]
                    }
                }
                                
                var stack = Stack()
                stack.push(1)
                stack.push(2)
                stack.push(3)
                Console.println(stack.pop())
                Console.println(stack.pop())
                stack.push(4)
                stack.push(5)
                Console.println(stack.top())
                Console.println(stack.pop())
                Console.println(stack.top())
                stack.pop()
                Console.println(stack.top())
                Console.println(stack.pop())
                """, """
                3
                2
                5
                5
                4
                1
                1
                """);
    }

    @Test
    public void testStringToInt() {
        verify("""
                func stringToInt(s) {
                    var result = 0
                    for (var i = 0; i < s.length(); ++i) {
                        var d = s.codeAt(i) - '0'.codeAt(0)
                        result = result * 10 + d
                    }
                    return result
                }
                                
                Console.println(stringToInt('0'))
                Console.println(stringToInt('2'))
                Console.println(stringToInt('123'))
                Console.println(stringToInt('32453532'))
                """, """
                0
                2
                123
                32453532
                """);
    }

    @Test
    public void testSubset() {
        verify("""
                func SubSet(nums) {
                    var result = []
                               
                    func dfs(index, set) {
                        if (index == nums.length()) {
                            result.addLast(set.copy())
                            return
                        }
                               
                        dfs(index + 1, set)
                               
                        set.addLast(nums[index])
                        dfs(index + 1, set)
                        set.removeLast()
                    }
                               
                    dfs(0, [])
                    return result
                }
                                
                var result = SubSet([1, 2, 3])
                for (var i = 0; i < result.length(); i++) {
                    Console.println(result[i])
                }
                """, """
                []
                [3]
                [2]
                [2, 3]
                [1]
                [1, 3]
                [1, 2]
                [1, 2, 3]
                """);
    }

    @Test
    public void testTwoSum() {
        verify("""
                func twoSum(nums, target) {
                    for (var i = 0; i < nums.length(); ++i) {
                        for (var j = i + 1; j < nums.length(); ++j) {
                            if (nums[i] + nums[j] == target) {
                                return [i, j]
                            }
                        }
                    }
                    return undefined
                }
                                
                Console.println(twoSum([2, 7, 11, 15], 9))
                Console.println(twoSum([3, 2, 4], 6))
                Console.println(twoSum([3, 3], 6))
                Console.println(twoSum([23, 16, 76, 97, 240, 224, 5, 78, 443, 25], 103))
                """, """
                [0, 1]
                [1, 2]
                [0, 1]
                [7, 9]
                """);
    }
}

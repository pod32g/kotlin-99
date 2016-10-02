package org.kotlin99

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.kotlin99.P57Test.Companion.treeEqualTo

fun <T> Tree<T>.convertToString(): String =
        if (this == End) {
            ""
        } else if (this is Node<T>) {
            value.toString() + if (left != End || right != End) {
                "(" + left.convertToString() + "," + right.convertToString() + ")"
            } else {
                ""
            }
        } else {
            throw UnknownTreeImplementation(this)
        }

fun String.convertToTree(): Tree<String> {
    fun String.drop(prefix: String): String =
            if (!startsWith(prefix)) throw IllegalStateException("Expected '$this' to start with '$prefix'")
            else drop(prefix.length)

    fun String.parse(): Pair<Tree<String>, Int> {
        val value = takeWhile { it != '(' && it != ',' && it != ')' }
        var rest = substring(value.length)
        if (value.isEmpty()) {
            return Pair(End, 0)
        } else if (!rest.startsWith("(")) {
            return Pair(Node(value), value.length)
        } else {
            rest = rest.drop("(")
            val (left, leftLength) = rest.parse()
            rest = rest.drop(leftLength).drop(",")
            val (right, rightLength) = rest.parse()
            rest.drop(rightLength).drop(")")
            return Pair(Node(value, left, right), value.length + leftLength + rightLength + 3)
        }
    }

    val (tree, ignored) = parse()
    return tree
}


class P67Test {
    @Test fun `conversion to string`() {
        assertThat(End.convertToString(), equalTo(""))
        assertThat(Node("a").convertToString(), equalTo("a"))
        assertThat(Node("a", Node("b"), Node("c")).convertToString(), equalTo("a(b,c)"))
        assertThat(Node("a", Node("b", Node("d"), Node("e")), Node("c", End, Node("f", Node("g"), End))).convertToString(), equalTo(
                "a(b(d,e),c(,f(g,)))"
        ))
    }

    @Test fun `conversion from string`() {
        assertThat("".convertToTree(), treeEqualTo<String>(End))
        assertThat("a".convertToTree(), treeEqualTo(Node("a")))
        assertThat("a(b,c)".convertToTree(), treeEqualTo(Node("a", Node("b"), Node("c"))))
        assertThat("a(b(d,e),c(,f(g,)))".convertToTree(), treeEqualTo(
                Node("a", Node("b", Node("d"), Node("e")), Node("c", End, Node("f", Node("g"), End)))
        ))
    }
}
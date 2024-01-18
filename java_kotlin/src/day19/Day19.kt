package day19

import java.io.File

class Day19 {
    private val finalDestinations = listOf("A", "R")
    private var workflowMap = mapOf<String, Workflow>()
    private val memoMap = mutableMapOf<Part, String>()

    fun part1(): Long {
        val rawString = File("inputs/day19.txt").readText()
        val inputParts = rawString.split("\n\n")
        assert(inputParts.size == 2)

        return solvePart1(inputParts)
    }

    fun solvePart1(inputs: List<String>): Long {
        val workflows = inputs.first().split("\n").map { parseWorkflowString(it) }
        val parts = inputs.last().split("\n").filter { it.isNotEmpty() }.map { Part.parseString(it) }

        workflowMap = workflows.associateBy { it.name }

        val accepted = mutableListOf<Part>()
        val rejected = mutableListOf<Part>()

        parts.forEach { part ->
            val result = processSinglePart(part)
            when (result) {
                "A" -> accepted.add(part)
                "R" -> rejected.add(part)
                else -> throw RuntimeException("Unknown final workflow")
            }
        }

        return accepted.sumOf { it.sum() }
    }

    private fun processSinglePart(
        part: Part
    ): String {
        var currWorkflowName = "in"

        while (!finalDestinations.contains(currWorkflowName)) {
            val currWorkflow = workflowMap[currWorkflowName]!!
            currWorkflowName = processRules(currWorkflow, part)

        }
        return currWorkflowName
    }

    fun solvePart2(inputs: List<String>): Long {
        val workflows = inputs.first().split("\n").map { parseWorkflowString(it) }
        val parts = inputs.last().split("\n").filter { it.isNotEmpty() }.map { Part.parseString(it) }

        workflowMap = workflows.associateBy { it.name }

        memoMap.clear()

        var count: Long = 0
        (1..4000L).forEach { x ->
            (1..4000L).forEach { m ->
                (1..4000L).forEach { a ->
                    (1..4000L).forEach { s ->
                        val result = memoProcessSinglePart(Part(x, m, a, s))
                        if (result == "A") {
                            count += 1
                        }
                    }
                }
            }
        }

        return count
    }

    private fun memoProcessSinglePart(part: Part): String {
        if (memoMap.containsKey(part)) {
            return memoMap[part]!!
        }
        val result = processSinglePart(part)
        memoMap[part] = result
        return result
    }

    private fun processRules(workflow: Workflow, part: Part): String {
        for (ruleStr in workflow.ruleStrings.subList(0, workflow.ruleStrings.size - 1)) {
            val rule = parseRuleString(ruleStr)
            when (rule.compare) {
                Compare.GREATER_THAN -> {
                    if (part.getRatingValue(rule.rating) > rule.threshold) {
                        return rule.destination
                    }
                }

                Compare.LESS_THAN -> {
                    if (part.getRatingValue(rule.rating) < rule.threshold) {
                        return rule.destination
                    }
                }
            }
        }

        return workflow.ruleStrings.last()
    }

    companion object {
        fun parseWorkflowString(string: String): Workflow {
            assert(string.contains("{"))
            assert(string.contains("}"))
            assert(string.contains(","))
            val workflowParts = string
                .replace("{", " ")
                .replace("}", " ")
                .trim()
                .split(" ")
            val name = workflowParts.first()
            val rules = workflowParts.last()
                .split(",")


            return Workflow(name, rules)
        }

        private fun parseRuleString(ruleStr: String): WorkflowRule {
            return if (ruleStr.contains(":")) {
                val rating = when (ruleStr[0]) {
                    'x' -> Rating.X
                    'm' -> Rating.M
                    'a' -> Rating.A
                    's' -> Rating.S
                    else -> throw RuntimeException("Unknown rating")
                }
                val compare = when (ruleStr[1]) {
                    '>' -> Compare.GREATER_THAN
                    '<' -> Compare.LESS_THAN
                    else -> throw RuntimeException("Unknown comparison")
                }
                val ruleParts = ruleStr
                    .replace("<", ":")
                    .replace(">", ":")
                    .split(":")
                val threshold = ruleParts[1].toLong()
                val destination = ruleParts[2]

                WorkflowRule(
                    rating, threshold, compare, destination
                )
            } else {
                throw RuntimeException("Final rule encountered instead of workflow rule")
            }
        }
    }


}


data class Workflow(val name: String, val ruleStrings: List<String>)

data class WorkflowRule(val rating: Rating, val threshold: Long, val compare: Compare, val destination: String) : Rule
enum class Rating { X, M, A, S }

enum class Compare { GREATER_THAN, LESS_THAN }

enum class FinalRule : Rule { Accepted, Rejected }

interface Rule

data class Part(val x: Long, val m: Long, val a: Long, val s: Long) {
    fun getRatingValue(rating: Rating): Long {
        return when (rating) {
            Rating.X -> this.x
            Rating.M -> this.m
            Rating.A -> this.a
            Rating.S -> this.s
        }
    }

    fun sum(): Long {
        return x + m + a + s
    }

    companion object {
        fun parseString(str: String): Part {
            val xmasParts = str
                .replace(Regex("[xmas]=|[{}]"), "")
                .split(",")
                .map { it.toLong() }
            return Part(xmasParts[0], xmasParts[1], xmasParts[2], xmasParts[3])
        }
    }
}
package day19

import java.io.File
import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min

class Day19 {
    private val finalDestinations = listOf("A", "R")
    private var workflowMap = mapOf<String, Workflow>()
    fun part1(): Long {
        val rawString = File("inputs/day19.txt").readText()
        val inputParts = rawString.split("\n\n")
        assert(inputParts.size == 2)

        return solvePart1(inputParts)
    }

    fun solvePart1(inputs: List<String>): Long {
        val workflows = inputs.first().split("\n").map { parseWorkflowString(it) }
        workflowMap = workflows.associateBy { it.name }

        val parts = inputs.last().split("\n").filter { it.isNotEmpty() }.map { Part.parseString(it) }


        val accepted = mutableListOf<Part>()
        val rejected = mutableListOf<Part>()


        parts.forEach { part ->
            var currWorkflowName = processSinglePart(part)
            when (currWorkflowName) {
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

    fun solvePart2(inputs: List<String>, upperLimit: Long = 4000): Long {
        val workflows = inputs.first().split("\n").map { parseWorkflowString(it) }
        workflowMap = workflows.associateBy { it.name }

        val startPartRange = PartRange(1..upperLimit, 1..upperLimit, 1..upperLimit, 1..upperLimit)
        val startState = TraceState("in", startPartRange)
        val statesToProcess = LinkedList<TraceState>()
        statesToProcess.add(startState)

        var accepted = 0L

        while (statesToProcess.isNotEmpty()) {
            val currState = statesToProcess.pop()!!
            val currWorkflow = workflowMap[currState.workflowName]!!
            val traceStates = processRules(currWorkflow, currState.partRange)
            traceStates.forEach {
                if (it.workflowName == "A") {
                    accepted += it.partRange.countPermutations()
                } else if (it.workflowName == "R") {
                    // skip these
                } else {
                    statesToProcess.add(it)
                }
            }
        }

        return accepted
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

    private fun processRules(workflow: Workflow, partRange: PartRange): List<TraceState> {
        val traceStates = mutableListOf<TraceState>()
        var remainingPartRange = partRange

        for (ruleStr in workflow.ruleStrings.subList(0, workflow.ruleStrings.size - 1)) {
            val rule = parseRuleString(ruleStr)
            val subdivideResult = remainingPartRange.subdivide(rule.compare, rule.rating, rule.threshold)
            traceStates.add(TraceState(rule.destination, subdivideResult.included))
            remainingPartRange = subdivideResult.excluded
        }

        traceStates.add(TraceState(workflow.ruleStrings.last(), remainingPartRange))

        return traceStates.filter { it.partRange.allNonZero() }
    }

    fun part2(): Long {
        val rawString = File("inputs/day19.txt").readText()
        val inputParts = rawString.split("\n\n")
        assert(inputParts.size == 2)

        return solvePart2(inputParts)
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

data class TraceState(val workflowName: String, val partRange: PartRange)

data class SubdivideResult(val included: PartRange, val excluded: PartRange)
data class PartRange(val x: LongRange, val m: LongRange, val a: LongRange, val s: LongRange) {
    fun allNonZero(): Boolean {
        return !x.isEmpty() && !m.isEmpty() && !a.isEmpty() && !s.isEmpty()
    }

    fun subdivide(compare: Compare, rating: Rating, threshold: Long): SubdivideResult {
        return when (compare) {
            Compare.GREATER_THAN -> {
                val actualThreshold = threshold + 1
                SubdivideResult(
                    included = handleGreaterThan(rating, actualThreshold),
                    excluded = handleLessThan(rating, threshold)
                )
            }

            Compare.LESS_THAN -> {
                val actualThreshold = threshold - 1
                SubdivideResult(
                    included = handleLessThan(rating, actualThreshold),
                    excluded = handleGreaterThan(rating, threshold)
                )
            }
        }
    }

    private fun handleGreaterThan(rating: Rating, actualThreshold: Long): PartRange {
        return when (rating) {
            Rating.X -> {
                val newBottom = max(actualThreshold, this.x.first)
                val newTop = max(newBottom, this.x.last)
                this.copy(x = newBottom..newTop)
            }

            Rating.M -> {
                val newBottom = max(actualThreshold, this.m.first)
                val newTop = max(newBottom, this.m.last)
                this.copy(m = newBottom..newTop)
            }

            Rating.A -> {
                val newBottom = max(actualThreshold, this.a.first)
                val newTop = max(newBottom, this.a.last)
                this.copy(a = newBottom..newTop)
            }

            Rating.S -> {
                val newBottom = max(actualThreshold, this.s.first)
                val newTop = max(newBottom, this.s.last)
                this.copy(s = newBottom..newTop)
            }
        }
    }

    private fun handleLessThan(rating: Rating, actualThreshold: Long): PartRange {
        return when (rating) {
            Rating.X -> {
                val newTop = min(actualThreshold, this.x.last)
                val newBottom = min(newTop, this.x.first)
                this.copy(x = newBottom..newTop)
            }

            Rating.M -> {
                val newTop = min(actualThreshold, this.m.last)
                val newBottom = min(newTop, this.m.first)
                this.copy(m = newBottom..newTop)
            }

            Rating.A -> {
                val newTop = min(actualThreshold, this.a.last)
                val newBottom = min(newTop, this.a.first)
                this.copy(a = newBottom..newTop)
            }

            Rating.S -> {
                val newTop = min(actualThreshold, this.s.last)
                val newBottom = min(newTop, this.s.first)
                this.copy(s = newBottom..newTop)
            }
        }

    }

    fun countPermutations(): Long {
        return x.count().toLong() * m.count().toLong() * a.count().toLong() * s.count().toLong()
    }
}

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
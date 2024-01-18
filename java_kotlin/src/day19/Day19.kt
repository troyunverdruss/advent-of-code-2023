package day19

import java.io.File

class Day19 {
    fun part1(): Long {
        val rawString = File("inputs/day19.txt").readText()
        val inputParts = rawString.split("\n\n")
        assert(inputParts.size == 2)

        val workflows = inputParts.first().split("\n").map { parseWorkflowString(it) }
        val parts = inputParts.last().split("\n").filter { it.isNotEmpty() }.map { Part.parseString(it) }

        val workflowMap = workflows.associateBy { it.name }


        val accepted = mutableListOf<Part>()
        val rejected = mutableListOf<Part>()

        val finalDestinations = listOf("A", "R")
        parts.forEach { part ->
            var currWorkflowName = "in"

            while (!finalDestinations.contains(currWorkflowName)) {
                val currWorkflow = workflowMap[currWorkflowName]!!
                currWorkflowName = processRules(currWorkflow, part)

            }
            when (currWorkflowName) {
                "A" -> accepted.add(part)
                "R" -> rejected.add(part)
                else -> throw RuntimeException("Unknown final workflow")
            }
        }

        return accepted.sumOf { it.sum() }
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
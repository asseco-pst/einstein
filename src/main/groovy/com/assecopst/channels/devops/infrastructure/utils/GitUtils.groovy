package com.asseco.pst.devops.infrastructure.utils

import java.util.regex.Matcher

class GitUtils {

    private GitUtils() {}


    static String getTagCommitSha(String aRepoSshUrl, String aTag) {

        String sha

        try {
            def process = "git ls-remote -t ${aRepoSshUrl} --match \"*${aTag}\"".execute() | "cut -b 1-40".execute()
            process.waitFor()

            sha = process.text.trim()

            if(!sha)
                throw new Exception("Unable to get commit sha for tag '${aTag}'.")
        } catch (e) {
        }

        return sha
    }

    static List<String> getTags(String aRepoSshUrl, String aGitVersionRegex) {

        def process = "git ls-remote -t ${aRepoSshUrl} --match \"*${aGitVersionRegex}\"".execute()
        process.waitFor()

        return cleanAndExtractTags(process.text)
    }


    private static List<String> cleanAndExtractTags(String aTagsList) {

        def excludePseudoTags = { line -> // removes the lines that contains '^{}' on it
            return !(line =~ /^.*\^\{}/).matches()
        }

        def extractTag = { line ->
            return ((String) line).tokenize("\t")[1]
        }

        return aTagsList.trim().tokenize("\n").stream().filter(excludePseudoTags).collect(extractTag)
    }
}

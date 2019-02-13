package com.assecopst.channels.devops.infrastructure.utils


class GitUtils {

    private GitUtils() {}


    static String getTagCommitSha(String aRepoSshUrl, String aTag) {

        String sha

        try {
            def process = "git ls-remote -t ${aRepoSshUrl} --match \"*${aTag}\"".execute() | "cut -b 1-40".execute()
            process.waitFor()

            sha = process.text.trim()

            if(!sha)
                throw new Exception("Commit sha for tag '${aTag}' is NULL.")
        } catch (e) {
            throw new Exception("Unable to get commit sha for tag '${aTag}'. Cause: ${e}")
        }

        return sha
    }

    static List<String> getTags(String aRepoSshUrl, String aGitVersionRegex = null) {

        String match = (aGitVersionRegex) ? "--match \"*${aGitVersionRegex}\"" : ""

        def process = "git ls-remote -t ${aRepoSshUrl} ${match}".execute()
        process.waitFor()

        return cleanAndExtractTags(process.text)
    }


    /**
     * Remove tag objects (the lines that contains the '^{}' characters)
     * and extract the tag version from each line result
     *
     * @param aTagsList
     * @return clean (i.e, the x.x.x string) tags from the given result
     */
    private static List<String> cleanAndExtractTags(String aTagsList) {

        def excludePseudoTags = { line ->
            return !(line =~ /^.*\^\{}/).matches()
        }

        def extractTag = { line ->
            return ((String) line.tokenize("\t")[1])
        }

        return aTagsList.trim().tokenize("\n").stream().filter(excludePseudoTags).collect(extractTag)
    }
}

package com.assecopst.channels.devops.infrastructure.utils

import com.assecopst.channels.devops.infrastructure.Project

class GitlabUtils {

    private static final String GITLAB_TOKEN = "GITLAB_TOKEN"
    private static final String gitlabToken = gitlabToken()

    private static String gitlabToken() {

        String token = System.getenv(GITLAB_TOKEN)
        if (!token)
            throw new Exception("Unable to get value from '${GITLAB_TOKEN}' environment variable")

        return token
    }

    static String getFileContentFromRepo(Project aProject) {

        FileFetcher fileFetcher = new FileFetcher(aProject.getRepoHttpsUrl(), aProject.versionCommitSha, Project.REQUIREMENTS_FILE)

        return fileFetcher.fetchFileContent()
    }


    /**
     * A helper class that allows to get files raw content from the gitlab repository
     *
     */
    static class FileFetcher {

        String repoHttpsUrl, commitHashcode, filename
        private String rawFileHttpsUrl

        FileFetcher(String aRepoHttpsUrl, String aCommitHashcode, String aFilename) {
            repoHttpsUrl = aRepoHttpsUrl
            commitHashcode = aCommitHashcode
            filename = aFilename

            setRawFileHttpsUrl()
        }

        private void setRawFileHttpsUrl() {
            rawFileHttpsUrl = [repoHttpsUrl, "raw", commitHashcode, filename].join("/")
        }

        private String getCurlCmd(boolean aIncludeHeaders = false) {

            String includeHeadersFlag = (aIncludeHeaders) ? "-I" : ""
            return "curl -k ${includeHeadersFlag} -H \"Private-Token: ${gitlabToken}\" ${rawFileHttpsUrl}"
        }

        String fetchFileContent() {

            String fileContent = ""

            if (fileExistsOnRepo()) {
                def process = getCurlCmd().execute()
                process.waitFor()

                fileContent = process.text.trim()
            } else {
                Console.warn("File ${rawFileHttpsUrl} does not exists!")
            }

            return fileContent
        }

        boolean fileExistsOnRepo() {

            try {
                def process = getCurlCmd(true).execute() | "head -n 1".execute() | "cut -b 8-10".execute()
                process.waitFor()

                int httpStatusCode = process.text.trim().toInteger()
                return (httpStatusCode == 200)

            } catch (e) {
                throw new Exception("Unable to check if file ${rawFileHttpsUrl} exists. Cause: ${e}")
            }
        }
    }
}

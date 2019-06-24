package com.pst.asseco.channels.devops.http
/**
 * This class wraps up the Repository Explorer instance, assuring that the same instance is used through all
 * the application
 */
abstract class RepoExplorerFactory {

    enum Type {
        GITLAB
    }

    private static RepositoryExplorer instance

    static RepositoryExplorer create(Type aRepoType, String aRepoUrl, String aToken) {

        switch (aRepoType) {
            case Type.GITLAB:
                instance = new GitLabRepositoryExplorer(aRepoUrl, aToken)
                break
//            case Type.GITHUB:
//                instance = new GitHubRepositoryExplorer(aRepoUrl, aToken)
//                break
        }

        return instance
    }

    /**
     * After the RepositoryExplorer is created, through the RepoExplorerFactory::create() method, it can be
     * collected using this method.
     *
     * @return the RepositoryExplorer instance
     */
    static RepositoryExplorer get() {
        if (!instance)
            throw new Exception("Cannot get the Repository Explorer. Please, instantiate one first...")
        return instance
    }
}

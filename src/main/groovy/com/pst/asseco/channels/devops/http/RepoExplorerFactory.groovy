package com.pst.asseco.channels.devops.http

import com.pst.asseco.channels.devops.infrastructure.utils.Console

/**
 * This class wraps up the Repository Explorer instance, assuring that the same instance is used through all
 * the application
 */
abstract class RepoExplorerFactory {

    enum Type {
        GITLAB
    }

    private static RepositoryExplorer instance

    /**
     * Creates a Gitlab Api explorer
     *
     * Currently, only Gitlab is supported. In the future,
     * by supporting more Repos, the Repository Explorer tool must be configured
     * by the user through an environment variable (i.e REPO_TOOL) and must be dynamically loaded on this method.
     *
     * @return Gitlab Api instance
     */
    static RepositoryExplorer create() {
        Console.debug("Instantiating Gitlab Api...")
        return create(Type.GITLAB)
    }

    static RepositoryExplorer create(Type aRepoType) {

        switch (aRepoType) {
            case Type.GITLAB:
                instance = new GitLabRepositoryExplorer()
                break
//            case Type.GITHUB:
//                instance = new GitHubRepositoryExplorer()
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

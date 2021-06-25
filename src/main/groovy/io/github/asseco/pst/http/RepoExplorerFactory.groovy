package io.github.asseco.pst.http

import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger


/**
 * This class wraps up the Repository Explorer instance, assuring that the same instance is used through all
 * the application
 */
abstract class RepoExplorerFactory {
    private static final Logger logger = LoggerFactory.getLogger(RepoExplorerFactory.class)
    private static Type DEFAULT_REPOSITORY_EXPLORER = Type.GITLAB
    private static RepositoryExplorer instance

    enum Type {
        GITLAB
    }

    static RepositoryExplorer create(Type aRepoType) {
        switch (aRepoType) {
            case Type.GITLAB:
                logger.debug("Instantiating Gitlab API connector...")
                instance = new GitLabRepositoryExplorer()
                break
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
        if (!instance){
            logger.warn("Could not get a Repository Explorer. Instantiating the default one: ${DEFAULT_REPOSITORY_EXPLORER.toString()}...")
            create(DEFAULT_REPOSITORY_EXPLORER)
        }
        return instance
    }
}

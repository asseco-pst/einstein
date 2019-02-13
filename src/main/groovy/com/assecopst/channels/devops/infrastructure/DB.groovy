package com.assecopst.channels.devops.infrastructure


class DB {
    enum Repos {

        A("git@gitlab.dcs.exictos.com:cegoncalves/runtime-requirements-project-a.git", "https://gitlab.dcs.exictos.com/cegoncalves/runtime-requirements-project-a"),
        B("git@gitlab.dcs.exictos.com:cegoncalves/runtime-requirements-project-b.git", "https://gitlab.dcs.exictos.com/cegoncalves/runtime-requirements-project-b"),
        C("git@gitlab.dcs.exictos.com:cegoncalves/runtime-requirements-project-c.git", "https://gitlab.dcs.exictos.com/cegoncalves/runtime-requirements-project-c")

        String sshUrl, httpsUrl

        Repos(String aRepoSshUrl, String aRepoHttpsUrl) {
            sshUrl = aRepoSshUrl
            httpsUrl = aRepoHttpsUrl
        }
    }
}

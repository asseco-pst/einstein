package com.pst.asseco.channels.devops.infrastructure


class DB {
    enum Repos {

        AAA("git@gitlab.dcs.exictos.com:cegoncalves/aaa.git", "https://gitlab.dcs.exictos.com/cegoncalves/aaa"),
        BBB("git@gitlab.dcs.exictos.com:cegoncalves/bbb.git", "https://gitlab.dcs.exictos.com/cegoncalves/bbb"),
        CCC("git@gitlab.dcs.exictos.com:cegoncalves/ccc.git", "https://gitlab.dcs.exictos.com/cegoncalves/ccc"),
        DDD("git@gitlab.dcs.exictos.com:cegoncalves/ddd.git", "https://gitlab.dcs.exictos.com/cegoncalves/ddd")

        String sshUrl, httpsUrl

        Repos(String aRepoSshUrl, String aRepoHttpsUrl) {
            sshUrl = aRepoSshUrl
            httpsUrl = aRepoHttpsUrl
        }
    }
}

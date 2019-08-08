package com.pst.asseco.channels.devops.infrastructure.utils

import java.util.regex.Matcher

/**
 * This class has some utils functions to facilitate the development. In the future the methods in this class
 * should move inside the appropriate classes
 */
class VersionUtils {

    static boolean isReleaseCandidate(String version){
        return ((Matcher) (version =~ /^(.)*?\"-rc\\\\.?([0-9]+)?\"$/)).matches()
    }

}

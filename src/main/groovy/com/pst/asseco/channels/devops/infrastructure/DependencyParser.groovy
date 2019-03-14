package com.pst.asseco.channels.devops.infrastructure


import com.pst.asseco.channels.devops.infrastructure.version.Version

import java.text.ParseException

class DependencyParser {

    private final String OPERATOR = "=~"
    private String record

    String projectNamespace
    String projectName
    String readVersion
    Version versionWrapper

    DependencyParser(String aRecord) {
        record = aRecord
        validateRecord()
        parse()
    }

    private void parse() {

        List<String> parsedRecord = record.split("(/|=~)").toList()

        projectNamespace = parsedRecord[0]
        projectName = parsedRecord[1]
        readVersion = parsedRecord[2]

        versionWrapper = Version.factory(readVersion)
    }

    private void validateRecord() {

        if (!record.contains("=~"))
            throw new ParseException("Dependency is missing an operator. Please use [project_id]$OPERATOR[version]", record.size())

    }
}

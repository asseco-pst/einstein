package com.asseco.pst.devops.infrastructure

import com.asseco.pst.devops.infrastructure.version.Version

class DependencyRecordParser {

    private final String operator = "~="
    private String record

    String projectName
    String readVersion
    Version versionWrapper

    DependencyRecordParser(String aRecord) {
        record = aRecord
        parse()
    }

    private void parse() {

        List<String> parsedRecord = splitRecord()

        projectName = parsedRecord[0]
        readVersion = parsedRecord[1]

        versionWrapper = Version.factory(readVersion)
    }

    private List<String> splitRecord() {

        if(!record.contains("~") || !record.contains("="))
            throw new Exception("Dependency record line isn't on the right syntax. Right syntax -> [project_id]~=[version]")

        return record.tokenize(operator)
    }
}

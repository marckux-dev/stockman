#!/usr/bin/env zsh
mvn test -Dspring.output.ansi.enabled=ALWAYS -Dstyle.color=always | grep --color=never 'TEST_RESULT\|Tests run'

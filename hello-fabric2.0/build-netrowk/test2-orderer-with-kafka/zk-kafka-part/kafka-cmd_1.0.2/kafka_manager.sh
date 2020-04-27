#!/usr/bin/env bash
echo "export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin" >> ~/.bashrc
source ~/.bashrc
/cmak/bin/cmak

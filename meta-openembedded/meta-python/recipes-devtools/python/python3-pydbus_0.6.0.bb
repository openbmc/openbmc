require python-pydbus.inc
inherit pypi setuptools3

SRC_URI += "file://0001-Support-asynchronous-calls-58.patch \
            file://0002-Support-transformation-between-D-Bus-errors-and-exce.patch \
"

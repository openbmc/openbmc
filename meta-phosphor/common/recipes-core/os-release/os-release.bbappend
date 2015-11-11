def git_describe(d):
        return bb.process.run("git describe --dirty")[0]
def git_tag(d):
        return bb.process.run("git tag")[0]

VERSION_ID = "${@git_tag(d)}"
BUILD_ID = "${@git_describe(d)}"
OS_RELEASE_FIELDS_append = " BUILD_ID"

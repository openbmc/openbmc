SUMMARY = "Selftest fixture: go_src_uri parameter variants"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Parsed via tinfoil for its GO_SRC_URI_* variables only; never actually built
# (it has no real SRC_URI, so do_go_vendor would fail).
EXCLUDE_FROM_WORLD = "1"

inherit go-vendor

GO_IMPORT = "example.com/testpkg"

GO_SRC_URI_BASIC     = "${@go_src_uri('github.com/foo/bar', 'v1.0.0')}"
GO_SRC_URI_PATH      = "${@go_src_uri('github.com/foo/bar', 'v2.1.0', path='github.com/foo/bar/v2')}"
GO_SRC_URI_PATHMAJOR = "${@go_src_uri('github.com/foo/bar', 'v2.0.0', pathmajor='/v2')}"
GO_SRC_URI_SUBDIR    = "${@go_src_uri('github.com/foo/bar', 'v1.0.0', subdir='sub/pkg')}"
GO_SRC_URI_REPLACES  = "${@go_src_uri('github.com/baz/qux', 'v1.0.0', replaces='../local/baz')}"
GO_SRC_URI_SVN       = "${@go_src_uri('example.com/svnmod', 'r42', vcs='svn')}"

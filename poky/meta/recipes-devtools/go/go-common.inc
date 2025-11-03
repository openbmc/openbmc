SUMMARY = "Go programming language compiler"
DESCRIPTION = " The Go programming language is an open source project to make \
 programmers more productive. Go is expressive, concise, clean, and\
 efficient. Its concurrency mechanisms make it easy to write programs\
 that get the most out of multicore and networked machines, while its\
 novel type system enables flexible and modular program construction.\
 Go compiles quickly to machine code yet has the convenience of\
 garbage collection and the power of run-time reflection. It's a\
 fast, statically typed, compiled language that feels like a\
 dynamically typed, interpreted language."

HOMEPAGE = " http://golang.org/"
LICENSE = "BSD-3-Clause"

inherit goarch

SRC_URI = "https://golang.org/dl/go${PV}.src.tar.gz;name=main"
S = "${WORKDIR}/go"
B = "${S}"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.src\.tar"

# all recipe variants are created from the same product
CVE_PRODUCT = "golang:go"
CVE_STATUS[CVE-2024-3566] = "not-applicable-platform: Issue only applies on Windows"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
SSTATE_SCAN_CMD = "true"

export GOROOT_OVERRIDE = "1"
export GOTMPDIR ?= "${WORKDIR}/build-tmp"
GOTMPDIR[vardepvalue] = ""
export CGO_ENABLED = "1"

export GOHOSTOS ?= "${BUILD_GOOS}"
export GOHOSTARCH ?= "${BUILD_GOARCH}"
export GOROOT_BOOTSTRAP ?= "${STAGING_LIBDIR_NATIVE}/go"
export GOOS ?= "${TARGET_GOOS}"
export GOARCH ?= "${TARGET_GOARCH}"
export GOARM ?= "${TARGET_GOARM}"
export GO386 ?= "${TARGET_GO386}"
export GOMIPS ?= "${TARGET_GOMIPS}"
export GOROOT_FINAL ?= "${libdir}/go"

export GODEBUG = "gocachehash=1"

do_compile:prepend() {
	BUILD_CC=${BUILD_CC}
}

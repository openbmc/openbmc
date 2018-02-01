require go-examples.inc

SRC_URI += " \
  file://helloworld.go \
"

do_compile() {
	go build helloworld.go
}

do_install() {
	install -D -m 0755 ${S}/helloworld ${D}${bindir}/helloworld
}

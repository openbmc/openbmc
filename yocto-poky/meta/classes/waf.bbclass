waf_do_configure() {
	${S}/waf configure --prefix=${prefix} ${EXTRA_OECONF}
}

waf_do_compile()  {
	${S}/waf build ${PARALLEL_MAKE}
}

waf_do_install() {
	${S}/waf install --destdir=${D}
}

EXPORT_FUNCTIONS do_configure do_compile do_install

# Build Class for Sip based Python Bindings
# (C) Michael 'Mickey' Lauer <mickey@Vanille.de>
#
STAGING_SIPDIR ?= "${STAGING_DATADIR_NATIVE}/sip"

DEPENDS  =+ "sip-native"
RDEPENDS_${PN} += "python-sip"

# default stuff, do not uncomment
# EXTRA_SIPTAGS = "-tWS_X11 -tQt_4_3_0"

# do_generate is before do_configure so ensure that sip_native is populated in sysroot before executing it
do_generate[depends] += "sip-native:do_populate_sysroot"

sip_do_generate() {
	if [ -z "${SIP_MODULES}" ]; then 
		MODULES="`ls sip/*mod.sip`"
	else
		MODULES="${SIP_MODULES}"
	fi

	if [ -z "$MODULES" ]; then
		die "SIP_MODULES not set and no modules found in $PWD"
        else
		bbnote "using modules '${SIP_MODULES}' and tags '${EXTRA_SIPTAGS}'"
	fi

	if [ -z "${EXTRA_SIPTAGS}" ]; then
		die "EXTRA_SIPTAGS needs to be set!"
	else
		SIPTAGS="${EXTRA_SIPTAGS}"
	fi

	if [ ! -z "${SIP_FEATURES}" ]; then
		FEATURES="-z ${SIP_FEATURES}"
		bbnote "sip feature file: ${SIP_FEATURES}"
	fi

	for module in $MODULES
	do
		install -d ${module}/
		echo "calling 'sip4 -I sip -I ${STAGING_SIPDIR} ${SIPTAGS} ${FEATURES} -c ${module} -b ${module}/${module}.pro.in sip/${module}/${module}mod.sip'"
		sip4 -I ${STAGING_SIPDIR} -I sip ${SIPTAGS} ${FEATURES} -c ${module} -b ${module}/${module}.sbf \
			sip/${module}/${module}mod.sip || die "Error calling sip on ${module}"
		sed -e 's,target,TARGET,' -e 's,sources,SOURCES,' -e 's,headers,HEADERS,' \
			${module}/${module}.sbf | sed s,"moc_HEADERS =","HEADERS +=", \
			>${module}/${module}.pro
		echo "TEMPLATE=lib" >>${module}/${module}.pro
		[ "${module}" = "qt" ] 		&& echo "" 		>>${module}/${module}.pro
		[ "${module}" = "qtcanvas" ] 	&& echo ""		>>${module}/${module}.pro
		[ "${module}" = "qttable" ] 	&& echo ""		>>${module}/${module}.pro
		[ "${module}" = "qwt" ] 	&& echo ""		>>${module}/${module}.pro
		[ "${module}" = "qtpe" ]        && echo ""		>>${module}/${module}.pro
		[ "${module}" = "qtpe" ]        && echo "LIBS+=-lqpe"	>>${module}/${module}.pro
		true
	done
}

EXPORT_FUNCTIONS do_generate

addtask generate after do_unpack do_patch before do_configure

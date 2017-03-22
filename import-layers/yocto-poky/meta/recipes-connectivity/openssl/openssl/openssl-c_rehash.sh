#!/bin/sh
#
# Ben Secrest <blsecres@gmail.com>
#
# sh c_rehash script, scan all files in a directory
# and add symbolic links to their hash values.
#
# based on the c_rehash perl script distributed with openssl
#
# LICENSE: See OpenSSL license
# ^^acceptable?^^
#

# default certificate location
DIR=/etc/openssl

# for filetype bitfield
IS_CERT=$(( 1 << 0 ))
IS_CRL=$(( 1 << 1 ))


# check to see if a file is a certificate file or a CRL file
# arguments:
#       1. the filename to be scanned
# returns:
#       bitfield of file type; uses ${IS_CERT} and ${IS_CRL}
#
check_file()
{
    local IS_TYPE=0

    # make IFS a newline so we can process grep output line by line
    local OLDIFS=${IFS}
    IFS=$( printf "\n" )

    # XXX: could be more efficient to have two 'grep -m' but is -m portable?
    for LINE in $( grep '^-----BEGIN .*-----' ${1} )
    do
	if echo ${LINE} \
	    | grep -q -E '^-----BEGIN (X509 |TRUSTED )?CERTIFICATE-----'
	then
	    IS_TYPE=$(( ${IS_TYPE} | ${IS_CERT} ))

	    if [ $(( ${IS_TYPE} & ${IS_CRL} )) -ne 0 ]
	    then
	    	break
	    fi
	elif echo ${LINE} | grep -q '^-----BEGIN X509 CRL-----'
	then
	    IS_TYPE=$(( ${IS_TYPE} | ${IS_CRL} ))

	    if [ $(( ${IS_TYPE} & ${IS_CERT} )) -ne 0 ]
	    then
	    	break
	    fi
	fi
    done

    # restore IFS
    IFS=${OLDIFS}

    return ${IS_TYPE}
}


#
# use openssl to fingerprint a file
#    arguments:
#	1. the filename to fingerprint
#	2. the method to use (x509, crl)
#    returns:
#	none
#    assumptions:
#	user will capture output from last stage of pipeline
#
fingerprint()
{
    ${SSL_CMD} ${2} -fingerprint -noout -in ${1} | sed 's/^.*=//' | tr -d ':'
}


#
# link_hash - create links to certificate files
#    arguments:
#       1. the filename to create a link for
#	2. the type of certificate being linked (x509, crl)
#    returns:
#	0 on success, 1 otherwise
#
link_hash()
{
    local FINGERPRINT=$( fingerprint ${1} ${2} )
    local HASH=$( ${SSL_CMD} ${2} -hash -noout -in ${1} )
    local SUFFIX=0
    local LINKFILE=''
    local TAG=''

    if [ ${2} = "crl" ]
    then
    	TAG='r'
    fi

    LINKFILE=${HASH}.${TAG}${SUFFIX}

    while [ -f ${LINKFILE} ]
    do
	if [ ${FINGERPRINT} = $( fingerprint ${LINKFILE} ${2} ) ]
	then
	    echo "NOTE: Skipping duplicate file ${1}" >&2
	    return 1
	fi	

	SUFFIX=$(( ${SUFFIX} + 1 ))
	LINKFILE=${HASH}.${TAG}${SUFFIX}
    done

    echo "${3} => ${LINKFILE}"

    # assume any system with a POSIX shell will either support symlinks or
    # do something to handle this gracefully
    ln -s ${3} ${LINKFILE}

    return 0
}


# hash_dir create hash links in a given directory
hash_dir()
{
    echo "Doing ${1}"

    cd ${1}

    ls -1 * 2>/dev/null | while read FILE
    do
        if echo ${FILE} | grep -q -E '^[[:xdigit:]]{8}\.r?[[:digit:]]+$' \
	    	&& [ -h "${FILE}" ]
        then
            rm ${FILE}
        fi
    done

    ls -1 *.pem *.cer *.crt *.crl 2>/dev/null | while read FILE
    do
	REAL_FILE=${FILE}
	# if we run on build host then get to the real files in rootfs
	if [ -n "${SYSROOT}" -a -h ${FILE} ]
	then
	    FILE=$( readlink ${FILE} )
	    # check the symlink is absolute (or dangling in other word)
	    if [ "x/" = "x$( echo ${FILE} | cut -c1 -)" ]
	    then
		REAL_FILE=${SYSROOT}/${FILE}
	    fi
	fi

	check_file ${REAL_FILE}
        local FILE_TYPE=${?}
	local TYPE_STR=''

        if [ $(( ${FILE_TYPE} & ${IS_CERT} )) -ne 0 ]
        then
            TYPE_STR='x509'
        elif [ $(( ${FILE_TYPE} & ${IS_CRL} )) -ne 0 ]
        then
            TYPE_STR='crl'
        else
            echo "NOTE: ${FILE} does not contain a certificate or CRL: skipping" >&2
	    continue
        fi

	link_hash ${REAL_FILE} ${TYPE_STR} ${FILE}
    done
}


# choose the name of an ssl application
if [ -n "${OPENSSL}" ]
then
    SSL_CMD=$(which ${OPENSSL} 2>/dev/null)
else
    SSL_CMD=/usr/bin/openssl
    OPENSSL=${SSL_CMD}
    export OPENSSL
fi

# fix paths
PATH=${PATH}:${DIR}/bin
export PATH

# confirm existance/executability of ssl command
if ! [ -x ${SSL_CMD} ]
then
    echo "${0}: rehashing skipped ('openssl' program not available)" >&2
    exit 0
fi

# determine which directories to process
old_IFS=$IFS
if [ ${#} -gt 0 ]
then
    IFS=':'
    DIRLIST=${*}
elif [ -n "${SSL_CERT_DIR}" ]
then
    DIRLIST=$SSL_CERT_DIR
else
    DIRLIST=${DIR}/certs
fi

IFS=':'

# process directories
for CERT_DIR in ${DIRLIST}
do
    if [ -d ${CERT_DIR} -a -w ${CERT_DIR} ]
    then
        IFS=$old_IFS
        hash_dir ${CERT_DIR}
        IFS=':'
    fi
done

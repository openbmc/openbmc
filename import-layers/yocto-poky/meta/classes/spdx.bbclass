# This class integrates real-time license scanning, generation of SPDX standard
# output and verifiying license info during the building process.
# It is a combination of efforts from the OE-Core, SPDX and Fossology projects.
#
# For more information on FOSSology:
#   http://www.fossology.org
#
# For more information on FOSSologySPDX commandline:
#   https://github.com/spdx-tools/fossology-spdx/wiki/Fossology-SPDX-Web-API
#
# For more information on SPDX:
#   http://www.spdx.org
#

# SPDX file will be output to the path which is defined as[SPDX_MANIFEST_DIR] 
# in ./meta/conf/licenses.conf.

SPDXSSTATEDIR = "${WORKDIR}/spdx_sstate_dir"

# If ${S} isn't actually the top-level source directory, set SPDX_S to point at
# the real top-level directory.
SPDX_S ?= "${S}"

python do_spdx () {
    import os, sys
    import json, shutil

    info = {} 
    info['workdir'] = d.getVar('WORKDIR')
    info['sourcedir'] = d.getVar('SPDX_S')
    info['pn'] = d.getVar('PN')
    info['pv'] = d.getVar('PV')
    info['spdx_version'] = d.getVar('SPDX_VERSION')
    info['data_license'] = d.getVar('DATA_LICENSE')

    sstatedir = d.getVar('SPDXSSTATEDIR')
    sstatefile = os.path.join(sstatedir, info['pn'] + info['pv'] + ".spdx")

    manifest_dir = d.getVar('SPDX_MANIFEST_DIR')
    info['outfile'] = os.path.join(manifest_dir, info['pn'] + ".spdx" )

    info['spdx_temp_dir'] = d.getVar('SPDX_TEMP_DIR')
    info['tar_file'] = os.path.join(info['workdir'], info['pn'] + ".tar.gz" )

    # Make sure important dirs exist
    try:
        bb.utils.mkdirhier(manifest_dir)
        bb.utils.mkdirhier(sstatedir)
        bb.utils.mkdirhier(info['spdx_temp_dir'])
    except OSError as e:
        bb.error("SPDX: Could not set up required directories: " + str(e))
        return

    ## get everything from cache.  use it to decide if 
    ## something needs to be rerun 
    cur_ver_code = get_ver_code(info['sourcedir'])
    cache_cur = False
    if os.path.exists(sstatefile):
        ## cache for this package exists. read it in
        cached_spdx = get_cached_spdx(sstatefile)

        if cached_spdx['PackageVerificationCode'] == cur_ver_code:
            bb.warn("SPDX: Verification code for " + info['pn']
                  + "is same as cache's. do nothing")
            cache_cur = True
        else:
            local_file_info = setup_foss_scan(info, True, cached_spdx['Files'])
    else:
        local_file_info = setup_foss_scan(info, False, None)

    if cache_cur:
        spdx_file_info = cached_spdx['Files']
        foss_package_info = cached_spdx['Package']
        foss_license_info = cached_spdx['Licenses']
    else:
        ## setup fossology command
        foss_server = d.getVar('FOSS_SERVER')
        foss_flags = d.getVar('FOSS_WGET_FLAGS')
        foss_full_spdx = d.getVar('FOSS_FULL_SPDX') == "true" or False
        foss_command = "wget %s --post-file=%s %s"\
            % (foss_flags, info['tar_file'], foss_server)
        
        foss_result = run_fossology(foss_command, foss_full_spdx)
        if foss_result is not None:
            (foss_package_info, foss_file_info, foss_license_info) = foss_result
            spdx_file_info = create_spdx_doc(local_file_info, foss_file_info)
            ## write to cache
            write_cached_spdx(sstatefile, cur_ver_code, foss_package_info,
                              spdx_file_info, foss_license_info)
        else:
            bb.error("SPDX: Could not communicate with FOSSology server. Command was: " + foss_command)
            return
    
    ## Get document and package level information
    spdx_header_info = get_header_info(info, cur_ver_code, foss_package_info)
    
    ## CREATE MANIFEST
    create_manifest(info, spdx_header_info, spdx_file_info, foss_license_info)

    ## clean up the temp stuff
    shutil.rmtree(info['spdx_temp_dir'], ignore_errors=True)
    if os.path.exists(info['tar_file']):
        remove_file(info['tar_file'])
}
addtask spdx after do_patch before do_configure

def create_manifest(info, header, files, licenses):
    import codecs
    with codecs.open(info['outfile'], mode='w', encoding='utf-8') as f:
        # Write header
        f.write(header + '\n')

        # Write file data
        for chksum, block in files.iteritems():
            f.write("FileName: " + block['FileName'] + '\n')
            for key, value in block.iteritems():
                if not key == 'FileName':
                    f.write(key + ": " + value + '\n')
            f.write('\n')

        # Write license data
        for id, block in licenses.iteritems():
            f.write("LicenseID: " + id + '\n')
            for key, value in block.iteritems():
                f.write(key + ": " + value + '\n')
            f.write('\n')

def get_cached_spdx(sstatefile):
    import json
    import codecs
    cached_spdx_info = {}
    with codecs.open(sstatefile, mode='r', encoding='utf-8') as f:
        try:
            cached_spdx_info = json.load(f)
        except ValueError as e:
            cached_spdx_info = None
    return cached_spdx_info

def write_cached_spdx(sstatefile, ver_code, package_info, files, license_info):
    import json
    import codecs
    spdx_doc = {}
    spdx_doc['PackageVerificationCode'] = ver_code
    spdx_doc['Files'] = {}
    spdx_doc['Files'] = files
    spdx_doc['Package'] = {}
    spdx_doc['Package'] = package_info
    spdx_doc['Licenses'] = {}
    spdx_doc['Licenses'] = license_info
    with codecs.open(sstatefile, mode='w', encoding='utf-8') as f:
        f.write(json.dumps(spdx_doc))

def setup_foss_scan(info, cache, cached_files):
    import errno, shutil
    import tarfile
    file_info = {}
    cache_dict = {}

    for f_dir, f in list_files(info['sourcedir']):
        full_path = os.path.join(f_dir, f)
        abs_path = os.path.join(info['sourcedir'], full_path)
        dest_dir = os.path.join(info['spdx_temp_dir'], f_dir)
        dest_path = os.path.join(info['spdx_temp_dir'], full_path)

        checksum = hash_file(abs_path)
        if not checksum is None:
            file_info[checksum] = {}
            ## retain cache information if it exists
            if cache and checksum in cached_files:
                file_info[checksum] = cached_files[checksum]
            ## have the file included in what's sent to the FOSSology server
            else:
                file_info[checksum]['FileName'] = full_path
                try:
                    bb.utils.mkdirhier(dest_dir)
                    shutil.copyfile(abs_path, dest_path)
                except OSError as e:
                    bb.warn("SPDX: mkdirhier failed: " + str(e))
                except shutil.Error as e:
                    bb.warn("SPDX: copyfile failed: " + str(e))
                except IOError as e:
                    bb.warn("SPDX: copyfile failed: " + str(e))
        else:
            bb.warn("SPDX: Could not get checksum for file: " + f)
    
    with tarfile.open(info['tar_file'], "w:gz") as tar:
        tar.add(info['spdx_temp_dir'], arcname=os.path.basename(info['spdx_temp_dir']))
    
    return file_info

def remove_file(file_name):
    try:
        os.remove(file_name)
    except OSError as e:
        pass

def list_files(dir):
    for root, subFolders, files in os.walk(dir):
        for f in files:
            rel_root = os.path.relpath(root, dir)
            yield rel_root, f
    return

def hash_file(file_name):
    try:
        with open(file_name, 'rb') as f:
            data_string = f.read()
            sha1 = hash_string(data_string)
            return sha1
    except:
        return None

def hash_string(data):
    import hashlib
    sha1 = hashlib.sha1()
    sha1.update(data)
    return sha1.hexdigest()

def run_fossology(foss_command, full_spdx):
    import string, re
    import subprocess

    try:
        foss_output = subprocess.check_output(foss_command.split(),
                stderr=subprocess.STDOUT).decode('utf-8')
    except subprocess.CalledProcessError as e:
        return None

    foss_output = string.replace(foss_output, '\r', '')

    # Package info
    package_info = {}
    if full_spdx:
        # All mandatory, only one occurrence
        package_info['PackageCopyrightText'] = re.findall('PackageCopyrightText: (.*?</text>)', foss_output, re.S)[0]
        package_info['PackageLicenseDeclared'] = re.findall('PackageLicenseDeclared: (.*)', foss_output)[0]
        package_info['PackageLicenseConcluded'] = re.findall('PackageLicenseConcluded: (.*)', foss_output)[0]
        # These may be more than one
        package_info['PackageLicenseInfoFromFiles'] = re.findall('PackageLicenseInfoFromFiles: (.*)', foss_output)
    else:
        DEFAULT = "NOASSERTION"
        package_info['PackageCopyrightText'] = "<text>" + DEFAULT + "</text>"
        package_info['PackageLicenseDeclared'] = DEFAULT
        package_info['PackageLicenseConcluded'] = DEFAULT
        package_info['PackageLicenseInfoFromFiles'] = []

    # File info
    file_info = {}
    records = []
    # FileName is also in PackageFileName, so we match on FileType as well.
    records = re.findall('FileName:.*?FileType:.*?</text>', foss_output, re.S)
    for rec in records:
        chksum = re.findall('FileChecksum: SHA1: (.*)\n', rec)[0]
        file_info[chksum] = {}
        file_info[chksum]['FileCopyrightText'] = re.findall('FileCopyrightText: '
            + '(.*?</text>)', rec, re.S )[0]
        fields = ['FileName', 'FileType', 'LicenseConcluded', 'LicenseInfoInFile']
        for field in fields:
            file_info[chksum][field] = re.findall(field + ': (.*)', rec)[0]

    # Licenses
    license_info = {}
    licenses = []
    licenses = re.findall('LicenseID:.*?LicenseName:.*?\n', foss_output, re.S)
    for lic in licenses:
        license_id = re.findall('LicenseID: (.*)\n', lic)[0]
        license_info[license_id] = {}
        license_info[license_id]['ExtractedText'] = re.findall('ExtractedText: (.*?</text>)', lic, re.S)[0]
        license_info[license_id]['LicenseName'] = re.findall('LicenseName: (.*)', lic)[0]

    return (package_info, file_info, license_info)

def create_spdx_doc(file_info, scanned_files):
    import json
    ## push foss changes back into cache
    for chksum, lic_info in scanned_files.iteritems():
        if chksum in file_info:
            file_info[chksum]['FileType'] = lic_info['FileType']
            file_info[chksum]['FileChecksum: SHA1'] = chksum
            file_info[chksum]['LicenseInfoInFile'] = lic_info['LicenseInfoInFile']
            file_info[chksum]['LicenseConcluded'] = lic_info['LicenseConcluded']
            file_info[chksum]['FileCopyrightText'] = lic_info['FileCopyrightText']
        else:
            bb.warn("SPDX: " + lic_info['FileName'] + " : " + chksum
                + " : is not in the local file info: "
                + json.dumps(lic_info, indent=1))
    return file_info

def get_ver_code(dirname):
    chksums = []
    for f_dir, f in list_files(dirname):
        hash = hash_file(os.path.join(dirname, f_dir, f))
        if not hash is None:
            chksums.append(hash)
        else:
            bb.warn("SPDX: Could not hash file: " + path)
    ver_code_string = ''.join(chksums).lower()
    ver_code = hash_string(ver_code_string)
    return ver_code

def get_header_info(info, spdx_verification_code, package_info):
    """
        Put together the header SPDX information.
        Eventually this needs to become a lot less
        of a hardcoded thing.
    """
    from datetime import datetime
    import os
    head = []
    DEFAULT = "NOASSERTION"

    package_checksum = hash_file(info['tar_file'])
    if package_checksum is None:
        package_checksum = DEFAULT

    ## document level information
    head.append("## SPDX Document Information")
    head.append("SPDXVersion: " + info['spdx_version'])
    head.append("DataLicense: " + info['data_license'])
    head.append("DocumentComment: <text>SPDX for "
        + info['pn'] + " version " + info['pv'] + "</text>")
    head.append("")

    ## Creator information
    ## Note that this does not give time in UTC.
    now = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')
    head.append("## Creation Information")
    ## Tools are supposed to have a version, but FOSSology+SPDX provides none.
    head.append("Creator: Tool: FOSSology+SPDX")
    head.append("Created: " + now)
    head.append("CreatorComment: <text>UNO</text>")
    head.append("")

    ## package level information
    head.append("## Package Information")
    head.append("PackageName: " + info['pn'])
    head.append("PackageVersion: " + info['pv'])
    head.append("PackageFileName: " + os.path.basename(info['tar_file']))
    head.append("PackageSupplier: Person:" + DEFAULT)
    head.append("PackageDownloadLocation: " + DEFAULT)
    head.append("PackageSummary: <text></text>")
    head.append("PackageOriginator: Person:" + DEFAULT)
    head.append("PackageChecksum: SHA1: " + package_checksum)
    head.append("PackageVerificationCode: " + spdx_verification_code)
    head.append("PackageDescription: <text>" + info['pn']
        + " version " + info['pv'] + "</text>")
    head.append("")
    head.append("PackageCopyrightText: "
        + package_info['PackageCopyrightText'])
    head.append("")
    head.append("PackageLicenseDeclared: "
        + package_info['PackageLicenseDeclared'])
    head.append("PackageLicenseConcluded: "
        + package_info['PackageLicenseConcluded'])

    for licref in package_info['PackageLicenseInfoFromFiles']:
        head.append("PackageLicenseInfoFromFiles: " + licref)
    head.append("")
    
    ## header for file level
    head.append("## File Information")
    head.append("")

    return '\n'.join(head)

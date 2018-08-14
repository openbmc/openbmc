#!/usr/bin/env python3
#
# Copyright (c) 2012 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
# DESCRIPTION
# This script is called by the SDK installer script. It replaces the dynamic
# loader path in all binaries and also fixes the SYSDIR paths/lengths and the
# location of ld.so.cache in the dynamic loader binary
#
# AUTHORS
# Laurentiu Palcu <laurentiu.palcu@intel.com>
#

import struct
import sys
import stat
import os
import re
import errno

if sys.version < '3':
    def b(x):
        return x
else:
    def b(x):
        return x.encode(sys.getfilesystemencoding())

old_prefix = re.compile(b("##DEFAULT_INSTALL_DIR##"))

def get_arch():
    f.seek(0)
    e_ident =f.read(16)
    ei_mag0,ei_mag1_3,ei_class = struct.unpack("<B3sB11x", e_ident)

    if (ei_mag0 != 0x7f and ei_mag1_3 != "ELF") or ei_class == 0:
        return 0

    if ei_class == 1:
        return 32
    elif ei_class == 2:
        return 64

def parse_elf_header():
    global e_type, e_machine, e_version, e_entry, e_phoff, e_shoff, e_flags,\
           e_ehsize, e_phentsize, e_phnum, e_shentsize, e_shnum, e_shstrndx

    f.seek(0)
    elf_header = f.read(64)

    if arch == 32:
        # 32bit
        hdr_fmt = "<HHILLLIHHHHHH"
        hdr_size = 52
    else:
        # 64bit
        hdr_fmt = "<HHIQQQIHHHHHH"
        hdr_size = 64

    e_type, e_machine, e_version, e_entry, e_phoff, e_shoff, e_flags,\
    e_ehsize, e_phentsize, e_phnum, e_shentsize, e_shnum, e_shstrndx =\
        struct.unpack(hdr_fmt, elf_header[16:hdr_size])

def change_interpreter(elf_file_name):
    if arch == 32:
        ph_fmt = "<IIIIIIII"
    else:
        ph_fmt = "<IIQQQQQQ"

    """ look for PT_INTERP section """
    for i in range(0,e_phnum):
        f.seek(e_phoff + i * e_phentsize)
        ph_hdr = f.read(e_phentsize)
        if arch == 32:
            # 32bit
            p_type, p_offset, p_vaddr, p_paddr, p_filesz,\
                p_memsz, p_flags, p_align = struct.unpack(ph_fmt, ph_hdr)
        else:
            # 64bit
            p_type, p_flags, p_offset, p_vaddr, p_paddr, \
            p_filesz, p_memsz, p_align = struct.unpack(ph_fmt, ph_hdr)

        """ change interpreter """
        if p_type == 3:
            # PT_INTERP section
            f.seek(p_offset)
            # External SDKs with mixed pre-compiled binaries should not get
            # relocated so look for some variant of /lib
            fname = f.read(11)
            if fname.startswith(b("/lib/")) or fname.startswith(b("/lib64/")) or \
               fname.startswith(b("/lib32/")) or fname.startswith(b("/usr/lib32/")) or \
               fname.startswith(b("/usr/lib32/")) or fname.startswith(b("/usr/lib64/")):
                break
            if p_filesz == 0:
                break
            if (len(new_dl_path) >= p_filesz):
                print("ERROR: could not relocate %s, interp size = %i and %i is needed." \
                    % (elf_file_name, p_memsz, len(new_dl_path) + 1))
                break
            dl_path = new_dl_path + b("\0") * (p_filesz - len(new_dl_path))
            f.seek(p_offset)
            f.write(dl_path)
            break

def change_dl_sysdirs(elf_file_name):
    if arch == 32:
        sh_fmt = "<IIIIIIIIII"
    else:
        sh_fmt = "<IIQQQQIIQQ"

    """ read section string table """
    f.seek(e_shoff + e_shstrndx * e_shentsize)
    sh_hdr = f.read(e_shentsize)
    if arch == 32:
        sh_offset, sh_size = struct.unpack("<16xII16x", sh_hdr)
    else:
        sh_offset, sh_size = struct.unpack("<24xQQ24x", sh_hdr)

    f.seek(sh_offset)
    sh_strtab = f.read(sh_size)

    sysdirs = sysdirs_len = ""

    """ change ld.so.cache path and default libs path for dynamic loader """
    for i in range(0,e_shnum):
        f.seek(e_shoff + i * e_shentsize)
        sh_hdr = f.read(e_shentsize)

        sh_name, sh_type, sh_flags, sh_addr, sh_offset, sh_size, sh_link,\
            sh_info, sh_addralign, sh_entsize = struct.unpack(sh_fmt, sh_hdr)

        name = sh_strtab[sh_name:sh_strtab.find(b("\0"), sh_name)]

        """ look only into SHT_PROGBITS sections """
        if sh_type == 1:
            f.seek(sh_offset)
            """ default library paths cannot be changed on the fly because  """
            """ the string lengths have to be changed too.                  """
            if name == b(".sysdirs"):
                sysdirs = f.read(sh_size)
                sysdirs_off = sh_offset
                sysdirs_sect_size = sh_size
            elif name == b(".sysdirslen"):
                sysdirslen = f.read(sh_size)
                sysdirslen_off = sh_offset
            elif name == b(".ldsocache"):
                ldsocache_path = f.read(sh_size)
                new_ldsocache_path = old_prefix.sub(new_prefix, ldsocache_path)
                new_ldsocache_path = new_ldsocache_path.rstrip(b("\0"))
                if (len(new_ldsocache_path) >= sh_size):
                    print("ERROR: could not relocate %s, .ldsocache section size = %i and %i is needed." \
                    % (elf_file_name, sh_size, len(new_ldsocache_path)))
                    sys.exit(-1)
                # pad with zeros
                new_ldsocache_path += b("\0") * (sh_size - len(new_ldsocache_path))
                # write it back
                f.seek(sh_offset)
                f.write(new_ldsocache_path)
            elif name == b(".gccrelocprefix"):
                offset = 0
                while (offset + 4096) <= sh_size:
                    path = f.read(4096)
                    new_path = old_prefix.sub(new_prefix, path)
                    new_path = new_path.rstrip(b("\0"))
                    if (len(new_path) >= 4096):
                        print("ERROR: could not relocate %s, max path size = 4096 and %i is needed." \
                        % (elf_file_name, len(new_path)))
                        sys.exit(-1)
                    # pad with zeros
                    new_path += b("\0") * (4096 - len(new_path))
                    #print "Changing %s to %s at %s" % (str(path), str(new_path), str(offset))
                    # write it back
                    f.seek(sh_offset + offset)
                    f.write(new_path)
                    offset = offset + 4096
    if sysdirs != "" and sysdirslen != "":
        paths = sysdirs.split(b("\0"))
        sysdirs = b("")
        sysdirslen = b("")
        for path in paths:
            """ exit the loop when we encounter first empty string """
            if path == b(""):
                break

            new_path = old_prefix.sub(new_prefix, path)
            sysdirs += new_path + b("\0")

            if arch == 32:
                sysdirslen += struct.pack("<L", len(new_path))
            else:
                sysdirslen += struct.pack("<Q", len(new_path))

        """ pad with zeros """
        sysdirs += b("\0") * (sysdirs_sect_size - len(sysdirs))

        """ write the sections back """
        f.seek(sysdirs_off)
        f.write(sysdirs)
        f.seek(sysdirslen_off)
        f.write(sysdirslen)

# MAIN
if len(sys.argv) < 4:
    sys.exit(-1)

# In python > 3, strings may also contain Unicode characters. So, convert
# them to bytes
if sys.version_info < (3,):
    new_prefix = sys.argv[1]
    new_dl_path = sys.argv[2]
else:
    new_prefix = sys.argv[1].encode()
    new_dl_path = sys.argv[2].encode()

executables_list = sys.argv[3:]

for e in executables_list:
    perms = os.stat(e)[stat.ST_MODE]
    if os.access(e, os.W_OK|os.R_OK):
        perms = None
    else:
        os.chmod(e, perms|stat.S_IRWXU)

    try:
        f = open(e, "r+b")
    except IOError:
        exctype, ioex = sys.exc_info()[:2]
        if ioex.errno == errno.ETXTBSY:
            print("Could not open %s. File used by another process.\nPlease "\
                  "make sure you exit all processes that might use any SDK "\
                  "binaries." % e)
        else:
            print("Could not open %s: %s(%d)" % (e, ioex.strerror, ioex.errno))
        sys.exit(-1)

    # Save old size and do a size check at the end. Just a safety measure.
    old_size = os.path.getsize(e)
    if old_size >= 64:
        arch = get_arch()
        if arch:
            parse_elf_header()
            change_interpreter(e)
            change_dl_sysdirs(e)

    """ change permissions back """
    if perms:
        os.chmod(e, perms)

    f.close()

    if old_size != os.path.getsize(e):
        print("New file size for %s is different. Looks like a relocation error!", e)
        sys.exit(-1)


#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# This file contains common functions for the fitimage generation

import os
import shlex
import subprocess
import bb

from oeqa.utils.commands import runCmd

class ItsNode:
    INDENT_SIZE = 8

    def __init__(self, name, parent_node, sub_nodes=None, properties=None):
        self.name = name
        self.parent_node = parent_node

        self.sub_nodes = []
        if sub_nodes:
            self.sub_nodes = sub_nodes

        self.properties = {}
        if properties:
            self.properties = properties

        if parent_node:
            parent_node.add_sub_node(self)

    def add_sub_node(self, sub_node):
        self.sub_nodes.append(sub_node)

    def add_property(self, key, value):
        self.properties[key] = value

    def emit(self, f, indent):
        indent_str_name = " " * indent
        indent_str_props = " " * (indent + self.INDENT_SIZE)
        f.write("%s%s {\n" % (indent_str_name, self.name))
        for key, value in self.properties.items():
            bb.debug(1, "key: %s, value: %s" % (key, str(value)))
            # Single integer: <0x12ab>
            if isinstance(value, int):
                f.write(indent_str_props + key + ' = <0x%x>;\n' % value)
            # list of strings: "string1", "string2" or integers: <0x12ab 0x34cd>
            elif isinstance(value, list):
                if len(value) == 0:
                    f.write(indent_str_props + key + ' = "";\n')
                elif isinstance(value[0], int):
                    list_entries = ' '.join('0x%x' % entry for entry in value)
                    f.write(indent_str_props + key + ' = <%s>;\n' % list_entries)
                else:
                    list_entries = ', '.join('"%s"' % entry for entry in value)
                    f.write(indent_str_props + key + ' = %s;\n' % list_entries)
            elif isinstance(value, str):
                # path: /incbin/("path/to/file")
                if key in ["data"] and value.startswith('/incbin/('):
                    f.write(indent_str_props + key + ' = %s;\n' % value)
                # Integers which are already string formatted
                elif value.startswith("<") and value.endswith(">"):
                    f.write(indent_str_props + key + ' = %s;\n' % value)
                else:
                    f.write(indent_str_props + key + ' = "%s";\n' % value)
            else:
                bb.fatal("%s has unexpexted data type." % str(value))
        for sub_node in self.sub_nodes:
            sub_node.emit(f, indent + self.INDENT_SIZE)
        f.write(indent_str_name + '};\n')

class ItsNodeImages(ItsNode):
    def __init__(self, parent_node):
        super().__init__("images", parent_node)

class ItsNodeConfigurations(ItsNode):
    def __init__(self, parent_node):
        super().__init__("configurations", parent_node)

class ItsNodeHash(ItsNode):
    def __init__(self, name, parent_node, algo, opt_props=None):
        properties = {
            "algo": algo
        }
        if opt_props:
            properties.update(opt_props)
        super().__init__(name, parent_node, None, properties)

class ItsImageSignature(ItsNode):
    def __init__(self, name, parent_node, algo, keyname, opt_props=None):
        properties = {
            "algo": algo,
            "key-name-hint": keyname
        }
        if opt_props:
            properties.update(opt_props)
        super().__init__(name, parent_node, None, properties)

class ItsNodeImage(ItsNode):
    def __init__(self, name, parent_node, description, type, compression, sub_nodes=None, opt_props=None):
        properties = {
            "description": description,
            "type": type,
            "compression": compression,
        }
        if opt_props:
            properties.update(opt_props)
        super().__init__(name, parent_node, sub_nodes, properties)

class ItsNodeDtb(ItsNodeImage):
    def __init__(self, name, parent_node, description, type, compression,
                 sub_nodes=None, opt_props=None, compatible=None):
        super().__init__(name, parent_node, description, type, compression, sub_nodes, opt_props)
        self.compatible = compatible

class ItsNodeDtbAlias(ItsNode):
    """Additional Configuration Node for a DTB

    Symlinks pointing to a DTB file are handled by an addtitional
    configuration node referring to another DTB image node.
    """
    def __init__(self, name, alias_name, compatible=None):
        super().__init__(name, parent_node=None, sub_nodes=None, properties=None)
        self.alias_name = alias_name
        self.compatible = compatible

class ItsNodeConfigurationSignature(ItsNode):
    def __init__(self, name, parent_node, algo, keyname, opt_props=None):
        properties = {
            "algo": algo,
            "key-name-hint": keyname
        }
        if opt_props:
            properties.update(opt_props)
        super().__init__(name, parent_node, None, properties)

class ItsNodeConfiguration(ItsNode):
    def __init__(self, name, parent_node, description, sub_nodes=None, opt_props=None):
        properties = {
            "description": description,
        }
        if opt_props:
            properties.update(opt_props)
        super().__init__(name, parent_node, sub_nodes, properties)

class ItsNodeRootKernel(ItsNode):
    """Create FIT images for the kernel

    Currently only a single kernel (no less or more) can be added to the FIT
    image along with 0 or more device trees and 0 or 1 ramdisk.

    If a device tree included in the FIT image, the default configuration is the
    firt DTB. If there is no dtb present than the default configuation the kernel.
    """
    def __init__(self, description, address_cells, host_prefix, arch, conf_prefix,
                 sign_enable=False, sign_keydir=None,
                 mkimage=None, mkimage_dtcopts=None,
                 mkimage_sign=None, mkimage_sign_args=None,
                 hash_algo=None, sign_algo=None, pad_algo=None,
                 sign_keyname_conf=None,
                 sign_individual=False, sign_keyname_img=None):
        props = {
            "description": description,
            "#address-cells": f"<{address_cells}>"
        }
        super().__init__("/", None, None, props)
        self.images = ItsNodeImages(self)
        self.configurations = ItsNodeConfigurations(self)

        self._host_prefix = host_prefix
        self._arch = arch
        self._conf_prefix = conf_prefix

        # Signature related properties
        self._sign_enable = sign_enable
        self._sign_keydir = sign_keydir
        self._mkimage = mkimage
        self._mkimage_dtcopts = mkimage_dtcopts
        self._mkimage_sign = mkimage_sign
        self._mkimage_sign_args = mkimage_sign_args
        self._hash_algo = hash_algo
        self._sign_algo = sign_algo
        self._pad_algo = pad_algo
        self._sign_keyname_conf = sign_keyname_conf
        self._sign_individual = sign_individual
        self._sign_keyname_img = sign_keyname_img
        self._sanitize_sign_config()

        self._dtbs = []
        self._dtb_alias = []
        self._kernel = None
        self._ramdisk = None
        self._bootscr = None
        self._setup = None

    def _sanitize_sign_config(self):
        if self._sign_enable:
            if not self._hash_algo:
                bb.fatal("FIT image signing is enabled but no hash algorithm is provided.")
            if not self._sign_algo:
                bb.fatal("FIT image signing is enabled but no signature algorithm is provided.")
            if not self._pad_algo:
                bb.fatal("FIT image signing is enabled but no padding algorithm is provided.")
            if not self._sign_keyname_conf:
                bb.fatal("FIT image signing is enabled but no configuration key name is provided.")
            if self._sign_individual and not self._sign_keyname_img:
                bb.fatal("FIT image signing is enabled for individual images but no image key name is provided.")

    def write_its_file(self, itsfile):
        with open(itsfile, 'w') as f:
            f.write("/dts-v1/;\n\n")
            self.emit(f, 0)

    def its_add_node_image(self, image_id, description, image_type, compression, opt_props):
        image_node = ItsNodeImage(
            image_id,
            self.images,
            description,
            image_type,
            compression,
            opt_props=opt_props
        )
        if self._hash_algo:
            ItsNodeHash(
                "hash-1",
                image_node,
                self._hash_algo
            )
        if self._sign_individual:
            ItsImageSignature(
                "signature-1",
                image_node,
                f"{self._hash_algo},{self._sign_algo}",
                self._sign_keyname_img
            )
        return image_node

    def its_add_node_dtb(self, image_id, description, image_type, compression, opt_props, compatible):
        dtb_node = ItsNodeDtb(
            image_id,
            self.images,
            description,
            image_type,
            compression,
            opt_props=opt_props,
            compatible=compatible
        )
        if self._hash_algo:
            ItsNodeHash(
                "hash-1",
                dtb_node,
                self._hash_algo
            )
        if self._sign_individual:
            ItsImageSignature(
                "signature-1",
                dtb_node,
                f"{self._hash_algo},{self._sign_algo}",
                self._sign_keyname_img
            )
        return dtb_node

    def fitimage_emit_section_kernel(self, kernel_id, kernel_path, compression,
        load, entrypoint, mkimage_kernel_type, entrysymbol=None):
        """Emit the fitImage ITS kernel section"""
        if self._kernel:
            bb.fatal("Kernel section already exists in the ITS file.")
        if entrysymbol:
            result = subprocess.run([self._host_prefix + "nm", "vmlinux"], capture_output=True, text=True)
            for line in result.stdout.splitlines():
                parts = line.split()
                if len(parts) == 3 and parts[2] == entrysymbol:
                    entrypoint = "<0x%s>" % parts[0]
                    break
        kernel_node = self.its_add_node_image(
            kernel_id,
            "Linux kernel",
            mkimage_kernel_type,
            compression,
            {
                "data": '/incbin/("' + kernel_path + '")',
                "arch": self._arch,
                "os": "linux",
                "load": f"<{load}>",
                "entry": f"<{entrypoint}>"
            }
        )
        self._kernel = kernel_node

    def fitimage_emit_section_dtb(self, dtb_id, dtb_path, dtb_loadaddress=None,
                                  dtbo_loadaddress=None, add_compatible=False):
        """Emit the fitImage ITS DTB section"""
        load=None
        dtb_ext = os.path.splitext(dtb_path)[1]
        if dtb_ext == ".dtbo":
            if dtbo_loadaddress:
                load = dtbo_loadaddress
        elif dtb_loadaddress:
            load = dtb_loadaddress

        opt_props = {
            "data": '/incbin/("' + dtb_path + '")',
            "arch": self._arch
        }
        if load:
            opt_props["load"] = f"<{load}>"

        # Preserve the DTB's compatible string to be added to the configuration node
        compatible = None
        if add_compatible:
            compatible = get_compatible_from_dtb(dtb_path)

        dtb_node = self.its_add_node_dtb(
            "fdt-" + dtb_id,
            "Flattened Device Tree blob",
            "flat_dt",
            "none",
            opt_props,
            compatible
        )
        self._dtbs.append(dtb_node)

    def fitimage_emit_section_dtb_alias(self, dtb_alias_id, dtb_path, add_compatible=False):
        """Add a configuration node referring to another DTB"""
        # Preserve the DTB's compatible string to be added to the configuration node
        compatible = None
        if add_compatible:
            compatible = get_compatible_from_dtb(dtb_path)

        dtb_id = os.path.basename(dtb_path)
        dtb_alias_node = ItsNodeDtbAlias("fdt-" + dtb_id, dtb_alias_id, compatible)
        self._dtb_alias.append(dtb_alias_node)
        bb.warn(f"compatible: {compatible}, dtb_alias_id: {dtb_alias_id}, dtb_id: {dtb_id}, dtb_path: {dtb_path}")

    def fitimage_emit_section_boot_script(self, bootscr_id, bootscr_path):
        """Emit the fitImage ITS u-boot script section"""
        if self._bootscr:
            bb.fatal("U-boot script section already exists in the ITS file.")
        bootscr_node = self.its_add_node_image(
            bootscr_id,
            "U-boot script",
            "script",
            "none",
            {
                "data": '/incbin/("' + bootscr_path + '")',
                "arch": self._arch,
                "type": "script"
            }
        )
        self._bootscr = bootscr_node

    def fitimage_emit_section_setup(self, setup_id, setup_path):
        """Emit the fitImage ITS setup section"""
        if self._setup:
            bb.fatal("Setup section already exists in the ITS file.")
        load = "<0x00090000>"
        entry = "<0x00090000>"
        setup_node = self.its_add_node_image(
            setup_id,
            "Linux setup.bin",
            "x86_setup",
            "none",
            {
                "data": '/incbin/("' + setup_path + '")',
                "arch": self._arch,
                "os": "linux",
                "load": load,
                "entry": entry
            }
        )
        self._setup = setup_node

    def fitimage_emit_section_ramdisk(self, ramdisk_id, ramdisk_path, description="ramdisk", load=None, entry=None):
        """Emit the fitImage ITS ramdisk section"""
        if self._ramdisk:
            bb.fatal("Ramdisk section already exists in the ITS file.")
        opt_props = {
            "data": '/incbin/("' + ramdisk_path + '")',
            "type": "ramdisk",
            "arch": self._arch,
            "os": "linux"
        }
        if load:
            opt_props["load"] = f"<{load}>"
        if entry:
            opt_props["entry"] = f"<{entry}>"

        ramdisk_node = self.its_add_node_image(
            ramdisk_id,
            description,
            "ramdisk",
            "none",
            opt_props
        )
        self._ramdisk = ramdisk_node

    def _fitimage_emit_one_section_config(self, conf_node_name, dtb=None):
        """Emit the fitImage ITS configuration section"""
        opt_props = {}
        conf_desc = []
        sign_entries = []

        if self._kernel:
            conf_desc.append("Linux kernel")
            opt_props["kernel"] = self._kernel.name
            if self._sign_enable:
                sign_entries.append("kernel")

        if dtb:
            conf_desc.append("FDT blob")
            opt_props["fdt"] = dtb.name
            if dtb.compatible:
                opt_props["compatible"] = dtb.compatible
            if self._sign_enable:
                sign_entries.append("fdt")

        if self._ramdisk:
            conf_desc.append("ramdisk")
            opt_props["ramdisk"] = self._ramdisk.name
            if self._sign_enable:
                sign_entries.append("ramdisk")

        if self._bootscr:
            conf_desc.append("u-boot script")
            opt_props["bootscr"] = self._bootscr.name
            if self._sign_enable:
                sign_entries.append("bootscr")

        if self._setup:
            conf_desc.append("setup")
            opt_props["setup"] = self._setup.name
            if self._sign_enable:
                sign_entries.append("setup")

        # First added configuration is the default configuration
        default_flag = "0"
        if len(self.configurations.sub_nodes) == 0:
            default_flag = "1"

        conf_node = ItsNodeConfiguration(
            conf_node_name,
            self.configurations,
            f"{default_flag} {', '.join(conf_desc)}",
            opt_props=opt_props
        )
        if self._hash_algo:
            ItsNodeHash(
                "hash-1",
                conf_node,
                self._hash_algo
            )
        if self._sign_enable:
            ItsNodeConfigurationSignature(
                "signature-1",
                conf_node,
                f"{self._hash_algo},{self._sign_algo}",
                self._sign_keyname_conf,
                opt_props={
                    "padding": self._pad_algo,
                    "sign-images": sign_entries
                }
            )

    def fitimage_emit_section_config(self, default_dtb_image=None):
        if self._dtbs:
            for dtb in self._dtbs:
                dtb_name = dtb.name
                if dtb.name.startswith("fdt-"):
                    dtb_name = dtb.name[len("fdt-"):]
                self._fitimage_emit_one_section_config(self._conf_prefix + dtb_name, dtb)
            for dtb in self._dtb_alias:
                self._fitimage_emit_one_section_config(self._conf_prefix + dtb.alias_name, dtb)
        else:
            # Currently exactly one kernel is supported.
            self._fitimage_emit_one_section_config(self._conf_prefix + "1")

        default_conf = self.configurations.sub_nodes[0].name
        if default_dtb_image and self._dtbs:
            default_conf = self._conf_prefix + default_dtb_image
        self.configurations.add_property('default', default_conf)

    def run_mkimage_assemble(self, itsfile, fitfile):
        cmd = [
            self._mkimage,
            '-f', itsfile,
            fitfile
        ]
        if self._mkimage_dtcopts:
            cmd.insert(1, '-D')
            cmd.insert(2, self._mkimage_dtcopts)
        try:
            subprocess.run(cmd, check=True, capture_output=True)
        except subprocess.CalledProcessError as e:
            bb.fatal(f"Command '{' '.join(cmd)}' failed with return code {e.returncode}\nstdout: {e.stdout.decode()}\nstderr: {e.stderr.decode()}\nitsflile: {os.path.abspath(itsfile)}")

    def run_mkimage_sign(self, fitfile):
        if not self._sign_enable:
            bb.debug(1, "FIT image signing is disabled. Skipping signing.")
            return

        # Some sanity checks because mkimage exits with 0 also without needed keys
        sign_key_path = os.path.join(self._sign_keydir, self._sign_keyname_conf)
        if not os.path.exists(sign_key_path + '.key') or not os.path.exists(sign_key_path + '.crt'):
            bb.fatal("%s.key or .crt does not exist" % sign_key_path)
        if self._sign_individual:
            sign_key_img_path = os.path.join(self._sign_keydir, self._sign_keyname_img)
            if not os.path.exists(sign_key_img_path + '.key') or not os.path.exists(sign_key_img_path + '.crt'):
                bb.fatal("%s.key or .crt does not exist" % sign_key_img_path)

        cmd = [
            self._mkimage_sign,
            '-F',
            '-k', self._sign_keydir,
            '-r', fitfile
        ]
        if self._mkimage_dtcopts:
            cmd.extend(['-D', self._mkimage_dtcopts])
        if self._mkimage_sign_args:
            cmd.extend(shlex.split(self._mkimage_sign_args))
        try:
            subprocess.run(cmd, check=True, capture_output=True)
        except subprocess.CalledProcessError as e:
            bb.fatal(f"Command '{' '.join(cmd)}' failed with return code {e.returncode}\nstdout: {e.stdout.decode()}\nstderr: {e.stderr.decode()}")


def symlink_points_below(file_or_symlink, expected_parent_dir):
    """returns symlink destination if it points below directory"""
    file_path = os.path.join(expected_parent_dir, file_or_symlink)
    if not os.path.islink(file_path):
        return None

    realpath = os.path.relpath(os.path.realpath(file_path), expected_parent_dir)
    if realpath.startswith(".."):
        return None

    return realpath

def get_compatible_from_dtb(dtb_path, fdtget_path="fdtget"):
    compatible = None
    cmd = [fdtget_path, "-t", "s", dtb_path, "/", "compatible"]
    try:
        ret = subprocess.run(cmd, check=True, capture_output=True, text=True)
        compatible = ret.stdout.strip().split()
    except subprocess.CalledProcessError:
        compatible = None
    return compatible

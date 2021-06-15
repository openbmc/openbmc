import argparse
import libfdt
import os
import sys

# Format: FEATURE : (dtb property, condition_operator, condition_value)
# If dtb property is None, then the item is always on
#
# If the condition_operator is None, then enable if it exists for existance
#
# If the condition_operator is '!', and condition_value is None then enable if
#    if is not defined
#
# Otherwise 'condition' and value are evaluated by type.
#
# If the condition is = then any value of condition_values will set it
# If the condition is ! then no value of condition_values will set it

microblaze_tune_features = {
  'microblaze' :      (None,                     None, None),
  'bigendian':        ('xlnx,endianness',        '!', 1),
  '64-bit' :          ('xlnx,data-size',         '=', 64),
  'barrel-shift':     ('xlnx,use-barrel',        '=', 1),
  'pattern-compare':  ('xlnx,use-pcmp-instr',    '=', 1),
  'reorder'   :       ('xlnx,use-reorder-instr', '!', 0),
  'frequency-optimized': ('xlnx,area-optimized', '=', 2),
  'multiply-low':     ('xlnx,use-hw-mul',        '=', 1),
  'multiply-high':    ('xlnx,use-hw-mul',        '=', 2),
  'divide-hard':      ('xlnx,use-div',           '=', 1),
  'fpu-soft':         ('xlnx,use-fpu',           '!', [1,2]),
  'fpu-hard':         ('xlnx,use-fpu',           '=', 1),
  'fpu-hard-extended':('xlnx,use-fpu',           '=', 2),
}

def processProperties(fdt, node):
    TUNE_FEATURES = []

    for feature in microblaze_tune_features:
        (property, cop, cvalue) = microblaze_tune_features[feature]

        if not property:
            TUNE_FEATURES.append(feature)

            # Special processing to get the version
            if feature == "microblaze":
                ver = microblazeVersion(fdt, node)
                if ver:
                    TUNE_FEATURES.append(ver)
            continue

        prop_value = fdt.getprop( node, property, libfdt.QUIET_NOTFOUND)

        if not prop_value or prop_value == -1:
            if cop == '!':
                if not cvalue:
                    TUNE_FEATURES.append(ver)
                    continue
            continue

        # If no operator
        if not cop or (cop == '=' and not cvalue):
            TUNE_FEATURES.append(feature)
            continue

        ctype = type(cvalue)
        if ctype == type(list()):
            val_list = cvalue
        else:
            val_list = [ cvalue ]

        result = False
        for value in val_list:
            ctype = type(value)
            if ctype == type(int()):
                val = prop_value.as_uint32()
            else:
                raise TypeError('Unknown type %s' % ctype)

            if value == val:
                result = True
                break
            else:
                result = False

        if (cop == '!' and result == False) or \
           (cop == '=' and result == True):
            TUNE_FEATURES.append(feature)

    return TUNE_FEATURES

def microblazeVersion(fdt, node):
    version = None

    val = fdt.getprop( node, 'model', libfdt.QUIET_NOTFOUND)

    if val and val != -1:
        val = fdt.getprop( node, 'model' ).as_str()
        version = val[val.find('microblaze,') + 11:]

        if version.startswith('8'):
            # Strip 8.xx.y, to just 8.xx
            v = version.split('.')
            version = '.'.join(v[0:2])

        version = 'v' + version

    return version

def MicroblazeConfig(dtbfile, out):
    fdt = libfdt.Fdt(open(dtbfile, mode='rb').read())

    cpu = -1
    while (True):
        cpu = cpu + 1
        try:
            node = fdt.path_offset('/cpus/cpu@%d' % cpu)

            try:
                prop = fdt.getprop( node, 'compatible' )

                prop_val = prop[:-1].decode('utf-8').split('\x00')

                microblaze = False
                for val in prop_val:
                    if "microblaze" in val:
                        microblaze = True
                        break

                if not microblaze:
                    continue

                # Construct TUNE_FEATURE here
                TUNE_FEATURES = processProperties(fdt, node)

                out.write('AVAILTUNES += "microblaze-cpu%s"\n' % (cpu))
                out.write('TUNE_FEATURES_tune-microblaze-cpu%s = "%s"\n' % (cpu, ' '.join(TUNE_FEATURES)))
                out.write('PACKAGE_EXTRA_ARCHS_tune-microblaze-cpu%s = "${TUNE_PKGARCH}"\n' % (cpu))

            except Exception as e:
                sys.stderr.write("Exception looking at properties: %s\n" % e)

                continue

        except Exception as e:
            # CPUs SHOULD be consecutive w/o gaps, so no more to search
            break

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Generate MicroBlaze TUNE_FEATURES')

    parser.add_argument('-d', '--dtb-file', action='store',
        help='DTB file to process')

    parser.add_argument('-o', '--output', action='store',
        help='Output file to store TUNE_FEATURE settings')

    args = parser.parse_args()

    if not args.dtb_file:
        sys.stderr.write('ERROR: You must specify a DTB_FILE to process.\n')
        sys.exit(1)

    outputf = sys.stdout
    if args.output:
        if os.path.exists(args.output):
            sys.stderr.write('ERROR: The output file "%s" exists!\n' % args.output)
            sys.exit(1)
        outputf = open(args.output, 'w')

    MicroblazeConfig(args.dtb_file, outputf)

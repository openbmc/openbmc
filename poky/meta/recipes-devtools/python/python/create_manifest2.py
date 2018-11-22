# This script is used as a bitbake task to create a new python manifest
# $ bitbake python -c create_manifest
#
# Our goal is to keep python-core as small as posible and add other python
# packages only when the user needs them, hence why we split upstream python
# into several packages.
#
# In a very simplistic way what this does is: 
# Launch python and see specifically what is required for it to run at a minimum
#
# Go through the python-manifest file and launch a separate task for every single
# one of the files on each package, this task will check what was required for that
# specific module to run, these modules will be called dependencies.
# The output of such task will be a list of the modules or dependencies that were
# found for that file.
#
# Such output will be parsed by this script, we will look for each dependency on the
# manifest and if we find that another package already includes it, then we will add
# that package as an RDEPENDS to the package we are currently checking; in case we dont
# find the current dependency on any other package we will add it to the current package
# as part of FILES.
#
#
# This way we will create a new manifest from the data structure that was built during
# this process, on this new manifest each package will contain specifically only
# what it needs to run.
#
# There are some caveats which we try to deal with, such as repeated files on different
# packages, packages that include folders, wildcards, and special packages.
# Its also important to note that this method only works for python files, and shared
# libraries. Static libraries, header files and binaries need to be dealt with manually.
#
# Author: Alejandro Enedino Hernandez Samaniego "aehs29" <aehs29 at gmail dot com>


import sys
import subprocess
import json
import os
import collections

# Hack to get native python search path (for folders), not fond of it but it works for now
pivot='recipe-sysroot-native'
for p in sys.path:
  if pivot in p:
    nativelibfolder=p[:p.find(pivot)+len(pivot)]

# Empty dict to hold the whole manifest
new_manifest = collections.OrderedDict()

# Check for repeated files, folders and wildcards
allfiles=[]
repeated=[]
wildcards=[]

hasfolders=[]
allfolders=[]

def isFolder(value):
  if os.path.isdir(value.replace('${libdir}',nativelibfolder+'/usr/lib')) or os.path.isdir(value.replace('${libdir}',nativelibfolder+'/usr/lib64')) or os.path.isdir(value.replace('${libdir}',nativelibfolder+'/usr/lib32')):
    return True
  else:
    return False

def prepend_comments(comments, json_manifest):
    with open(json_manifest, 'r+') as manifest:
        json_contents = manifest.read()
        manifest.seek(0, 0)
        manifest.write(comments + json_contents)

# Read existing JSON manifest
with open('python2-manifest.json') as manifest:
    # The JSON format doesn't allow comments so we hack the call to keep the comments using a marker
    manifest_str =  manifest.read()
    json_start = manifest_str.find('# EOC') + 6 # EOC + \n
    manifest.seek(0)
    comments = manifest.read(json_start)
    manifest_str = manifest.read()
    old_manifest = json.loads(manifest_str, object_pairs_hook=collections.OrderedDict)

# First pass to get core-package functionality, because we base everything on the fact that core is actually working
# Not exactly the same so it should not be a function
print ("Getting dependencies for core package:")

# Special call to check for core package
output = subprocess.check_output([sys.executable, 'get_module_deps2.py', 'python-core-package'])
for item in output.split():
    # We append it so it doesnt hurt what we currently have:
    if item not in old_manifest['core']['files']:
        # We use the same data structure since its the one which will be used to check
        # dependencies for other packages
        old_manifest['core']['files'].append(item)

for value in old_manifest['core']['files']:
  # Ignore folders, since we don't import those, difficult to handle multilib
  if isFolder(value):
    # Pass it directly
    if value not in old_manifest['core']['files']:
      old_manifest['core']['files'].append(value)
  # Ignore binaries, since we don't import those, assume it was added correctly (manually)
  if '${bindir}' in value:
    # Pass it directly
    if value not in old_manifest['core']['files']:
      old_manifest['core']['files'].append(value)
    continue
  # Ignore empty values
  if value == '':
    continue
  if '${includedir}' in value:
    if value not in old_manifest['core']['files']:
      old_manifest['core']['files'].append(value)
    continue
  # Get module name , shouldnt be affected by libdir/bindir
  value = os.path.splitext(os.path.basename(os.path.normpath(value)))[0]


  # Launch separate task for each module for deterministic behavior
  # Each module will only import what is necessary for it to work in specific
  print ('Getting dependencies for module: %s' % value)
  output = subprocess.check_output([sys.executable, 'get_module_deps2.py', '%s' % value])
  for item in output.split():
    # We append it so it doesnt hurt what we currently have:
    if item not in old_manifest['core']['files']:
      old_manifest['core']['files'].append(item)

# We check which packages include folders
for key in old_manifest:
    for value in old_manifest[key]['files']:
        # Ignore folders, since we don't import those, difficult to handle multilib
        if isFolder(value):
            print ('%s is a folder' % value)
            if key not in hasfolders:
                hasfolders.append(key)
            if value not in allfolders:
                allfolders.append(value)

for key in old_manifest:
    # Use an empty dict as data structure to hold data for each package and fill it up
    new_manifest[key] = collections.OrderedDict()
    new_manifest[key]['summary'] = old_manifest[key]['summary']
    new_manifest[key]['rdepends']=[]
    new_manifest[key]['files'] = []

    # All packages should depend on core
    if key != 'core':
        new_manifest[key]['rdepends'].append('core')

    # Handle special cases, we assume that when they were manually added 
    # to the manifest we knew what we were doing.
    print ('Handling package %s' % key)
    special_packages=['misc', 'modules', 'tests', 'dev']
    if key in special_packages or 'staticdev' in key:
        print('Passing %s package directly' % key)
        new_manifest[key]=old_manifest[key]
        continue

    for value in old_manifest[key]['files']:
        # We already handled core on the first pass
        if key == 'core':
            new_manifest[key]['files'].append(value)
            continue
        # Ignore folders, since we don't import those, difficult to handle multilib
        if isFolder(value):
            # Pass folders directly
            new_manifest[key]['files'].append(value)
        # Ignore binaries, since we don't import those
        if '${bindir}' in value:
            # Pass it directly to the new manifest data structure
            if value not in new_manifest[key]['files']:
                new_manifest[key]['files'].append(value)
            continue
        # Ignore empty values
        if value == '':
            continue
        if '${includedir}' in value:
            if value not in new_manifest[key]['files']:
                new_manifest[key]['files'].append(value)
            continue
        # Get module name , shouldnt be affected by libdir/bindir
        value = os.path.splitext(os.path.basename(os.path.normpath(value)))[0]

        # Launch separate task for each module for deterministic behavior
        # Each module will only import what is necessary for it to work in specific
        print ('Getting dependencies for module: %s' % value)
        output = subprocess.check_output([sys.executable, 'get_module_deps2.py', '%s' % value])

        # We can print dependencies for debugging purposes
        #print (output)
        # Output will have all dependencies
        for item in output.split():

            # Warning: This first part is ugly
            # One of the dependencies that was found, could be inside of one of the folders included by another package
            # We need to check if this happens so we can add the package containing the folder as an RDEPENDS
            # e.g. Folder encodings contained in codecs
            # This would be solved if no packages included any folders

            # This can be done in two ways:
            # 1 - We assume that if we take out the filename from the path we would get
            #   the folder string, then we would check if folder string is in the list of folders
            #   This would not work if a package contains a folder which contains another folder
            #   e.g. path/folder1/folder2/filename  folder_string= path/folder1/folder2
            #   folder_string would not match any value contained in the list of folders
            #
            # 2 - We do it the other way around, checking if the folder is contained in the path
            #   e.g. path/folder1/folder2/filename  folder_string= path/folder1/folder2
            #   is folder_string inside path/folder1/folder2/filename?, 
            #   Yes, it works, but we waste a couple of milliseconds.

            inFolders=False
            for folder in allfolders:
                if folder in item:
                    inFolders = True # Did we find a folder?
                    folderFound = False # Second flag to break inner for
                    # Loop only through packages which contain folders
                    for keyfolder in hasfolders:
                        if (folderFound == False):
                            #print("Checking folder %s on package %s" % (item,keyfolder))
                            for file_folder in old_manifest[keyfolder]['files']:
                                if file_folder==folder:
                                    print ('%s found in %s' % (folder, keyfolder))
                                    folderFound = True
                                    if keyfolder not in new_manifest[key]['rdepends'] and keyfolder != key:
                                        new_manifest[key]['rdepends'].append(keyfolder)
                        else:
                            break

            # A folder was found so we're done with this item, we can go on
            if inFolders:
                continue

            # We might already have it on the dictionary since it could depend on a (previously checked) module
            if item not in new_manifest[key]['files']:
                # Handle core as a special package, we already did it so we pass it to NEW data structure directly
                if key=='core':
                  print('Adding %s to %s FILES' % (item, key))
                  if item.endswith('*'):
                      wildcards.append(item)
                  new_manifest[key]['files'].append(item)

                  # Check for repeated files
                  if item not in allfiles:
                      allfiles.append(item)
                  else:
                      repeated.append(item)

                else:

                    # Check if this dependency is already contained on another package, so we add it
                    # as an RDEPENDS, or if its not, it means it should be contained on the current
                    # package, so we should add it to FILES
                    for newkey in old_manifest:
                        # Debug
                        #print("Checking %s " % item + " in %s" % newkey)
                        if item in old_manifest[newkey]['files']:      
                                # Since were nesting, we need to check its not the same key
                                if(newkey!=key):
                                    if newkey not in new_manifest[key]['rdepends']:
                                       # Add it to the new manifest data struct
                                       # Debug
                                       print('Adding %s to %s RDEPENDS, because it contains %s' % (newkey, key, item))
                                       new_manifest[key]['rdepends'].append(newkey)
                                    break
                    else:
                      # Debug
                      print('Adding %s to %s FILES' % (item, key))
                      # Since it wasnt found on another package, its not an RDEP, so add it to FILES for this package
                      new_manifest[key]['files'].append(item)
                      if item.endswith('*'):
                          wildcards.append(item)
                      if item not in allfiles:
                          allfiles.append(item)
                      else:
                          repeated.append(item)

print ('The following files are repeated (contained in more than one package), please check which package should get it:')
print (repeated)
print('The following files contain wildcards, please check they are necessary')
print(wildcards)
print('The following files contain folders, please check they are necessary')
print(hasfolders)

# Sort it just so it looks nice 
for key in new_manifest:
    new_manifest[key]['files'].sort()
    new_manifest[key]['rdepends'].sort()

# Create the manifest from the data structure that was built
with open('python2-manifest.json.new','w') as outfile:
    json.dump(new_manifest,outfile, indent=4)

prepend_comments(comments,'python2-manifest.json.new')

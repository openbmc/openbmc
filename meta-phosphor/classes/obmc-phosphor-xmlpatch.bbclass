#This class applies patches to an XML file during do_patch().  The
#patches themselves are specified in XML in a separate file that must
#be in SRC_URI and end in .patch.xml.
#
#This patch file can have any root element name, and uses XML attributes
#to specify if the elements in the file should replace existing elements
#or add new ones.  An XPath attribute is used to specify where the fix
#should be applied.  A <targetFile> element is required in the patch file
#to specify the base name of the XML file the patches should be applied to.
#
#The only restriction is that since the type, xpath, and key attributes are
#used to specify the patch placement the target XML cannot use those at a
#top level element.
#
# It can apply patches in 5 ways:
#
# 1) Add an element:
#    Put in the element to add, along with the type='add' attribute
#    and an xpath attribute specifying where the new element should go.
#
#     <enumerationType type='add' xpath="./">
#       <id>MY_TYPE</id>
#     </enumerationType>
#
#     This will add a new enumerationType element child to root element
#
# 2) Replace an element:
#    Put in the new element, with the type='replace' attribute
#    and the XPath of the element you want to replace.
#
#     <enumerator type='replace'
#               xpath="enumerationType/[id='TYPE']/enumerator[name='XBUS']">
#       <name>XBUS</name>
#       <value>the new XBUS value</value>
#     </enumerator>
#
#    This will replace the enumerator element with name XBUS under the
#    TYPE enumerationType
#
# 3) Remove an element:
#    Put in the element to remove, with the type='remove' attribute and
#    the XPath of the element you want to remove. The full element contents
#    don't need to be specified, as the XPath is what locates the element.
#
#    <enumerator type='remove'
#                xpath='enumerationType[id='TYPE]/enumerator[name='DIMM']>
#    </enumerator>
#
# 4) Add child elements to a specific element.  Useful when adding several
#    child elements at once.
#
#    Use a type attribute of 'add-child' and specify the target parent with
#    the xpath attribute.
#
#     <enumerationType type="add-child" xpath="enumerationType/[id='TYPE']">
#       <enumerator>
#         <name>MY_NEW_ENUMERATOR</name>
#         <value>23</value>
#       </enumerator>
#       <enumerator>
#         <name>ANOTHER_NEW_ENUMERATOR</name>
#         <value>99</value>
#       </enumerator>
#     </enumerationType>
#
#     This will add 2 new <enumerator> elements to the enumerationType
#     element with ID TYPE.
#
# 5) Replace a child element inside another element, useful when replacing
#    several child elements of the same parent at once.
#
#    Use a type attribute of 'replace-child' and the xpath attribute
#    as described above, and also use the key attribute to specify which
#    element should be used to match on so the replace can be done.
#
#     <enumerationType type="replace-child"
#                      key="name"
#                      xpath="enumerationType/[id='TYPE']">
#       <enumerator>
#         <name>OLD_ENUMERATOR</name>
#         <value>newvalue</value>
#       </enumerator>
#       <enumerator>
#         <name>ANOTHER_OLD_ENUMERATOR</name>
#         <value>anothernewvalue</value>
#       </enumerator>
#     </enumerationType>
#
#     This will replace the <enumerator> elements with the names of
#     OLD_ENUMERATOR and ANOTHER_OLD_ENUMERATOR with the <enumerator>
#     elements specified, inside of the enumerationType element with
#     ID TYPE.


def deleteAttrs(element, attrs):
    for a in attrs:
        try:
            del element.attrib[a]
        except:
            pass


def findPatchFiles(d):
    patches = []
    for f in d.getVar("SRC_URI", True).split():
        f = f[f.find("//") + 2:]
        if f.find(".patch.xml") != -1:
            patches.append(f)

    return patches


def applyXMLPatch(basePatchName, d):
    from lxml import etree

    patchFile = os.path.join(d.getVar("WORKDIR", True), basePatchName)

    if not os.path.exists(patchFile):
        print("Could not find patch file " + patchFile +
              " specified in SRC_URI")
        sys.exit(-1)

    patchTree = etree.parse(patchFile)
    patchRoot = patchTree.getroot()

    targetFileElement = patchRoot.find("targetFile")
    if targetFileElement is None:
        print("Could not find <targetFile> element in patch file "
              + patchFile)
        sys.exit(-1)
    else:
        xml = targetFileElement.text

    xml = os.path.join(d.getVar("S", True), xml)

    if not os.path.exists(xml):
        print "Could not find XML file to patch: " + xml
        sys.exit(-1)

    rc = 0
    patchNum = 0
    tree = etree.parse(xml)
    root = tree.getroot()

    print("Applying XML fixes found in " + patchFile + " to " + xml)

    for node in patchRoot:
        if (node.tag is etree.PI) or (node.tag is etree.Comment) or \
           (node.tag == "targetFile"):
            continue

        xpath = node.get('xpath', None)
        patchType = node.get('type', 'add')
        patchKey = node.get('key', None)
        deleteAttrs(node, ['xpath', 'type', 'key'])

        if xpath is None:
            print("No XPath attribute found for patch " + str(patchNum))
            rc = -1
        else:
            target = tree.find(xpath)

            if target is None:
                print("Patch " + str(patchNum) + ": Could not find XPath "
                      "target " + xpath)
                rc = -1
            else:
                print("Patch " + str(patchNum) + ":")

            if patchType == "add":

                print("  Adding element " + target.tag + " to " + xpath)

                #The ServerWiz API is dependent on ordering for the elements
                #at the root node, so make sure they get appended at the end
                if (xpath == "./") or (xpath == "/"):
                    root.append(node)
                else:
                    target.append(node)

            elif patchType == "remove":

                print("  Removing element " + xpath)
                parent = target.find("..")
                if parent is None:
                    print("Could not find parent of " + xpath +
                          " so can't remove this element")
                    rc = -1
                else:
                    parent.remove(target)

            elif patchType == "replace":

                print("  Replacing element " + xpath)
                parent = target.find("..")
                if parent is None:
                    print("Could not find parent of " + xpath +
                          " so can't replace this element")
                    rc = -1
                else:
                    parent.remove(target)
                    parent.append(node)

            elif patchType == "add-child":

                for child in node:
                    print("  Adding a '" + child.tag + "' child element to " +
                          xpath)
                    target.append(child)

            elif patchType == "replace-child":

                if patchKey is not None:
                    updates = []
                    for child in node:
                        #Use the key to figure out which element to replace
                        keyElement = child.find(patchKey)
                        for targetChild in target:
                            for grandchild in targetChild:
                                if (grandchild.tag == patchKey) and \
                                   (grandchild.text == keyElement.text):
                                    update = {}
                                    update['remove'] = targetChild
                                    update['add'] = child
                                    updates.append(update)

                    for update in updates:
                        print("  Replacing a '" + update['remove'].tag +
                              "' element in path " + xpath)
                        target.remove(update['remove'])
                        target.append(update['add'])

                else:
                    print("Patch type is replace-child, but 'key' "
                          "attribute isn't set")
                    rc = -1

        patchNum = patchNum + 1

        if rc == 0:
            os.rename(xml, xml + ".orig")
            tree.write(xml)
        else:
            sys.exit(-1)


python do_patch() {

    for patch in findPatchFiles(d):
        applyXMLPatch(patch, d)
}

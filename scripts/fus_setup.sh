# Setup script to initialize the layer.
# This script will be called by fus-setup-release.sh which is located in the yocto root folder

# Add layer to bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-fus-updater-azure \"" >> $BUILD_DIR/conf/bblayers.conf

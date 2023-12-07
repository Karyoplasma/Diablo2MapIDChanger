# Diablo2MapIDChanger
Change the map seed of your Diablo 2 LOD character through a streamlined GUI.

The tool automatically backs up your character before making any attempt to change the map seed. The backup will be found in a subfolder of your save folder called "backupMapIDChanger". The backup will be called %CharName%.d2s_%unixtimestamp%. It will never remove any backups, so if you use it extremely frequently, you should clear out the backup folder from time to time.

The auto-backup never failed me, but if you want to be extra safe, create your own backup.

A neat little side-effect of the routine the map changer uses is that it will fix characters with a bad checksum, so if you get a "Generic bad file" message in D2, try changing the character's map seed with this tool. If a bad checksum was the culprit, it will fix the issue.

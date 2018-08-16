GIT Publish
===================

Windows: copy all files except compiled classes and .snv dirs

	robocopy D:\eclipse\projects_r01fb\r01fb\ D:\eclipse\projects_r01fb\r01fb_git\ * /s /xd classes .svn
	
	robocopy D:\eclipse\projects_platea\r01hp\ D:\eclipse\projects_platea\r01hp_git\ * /s /xd classes .svn
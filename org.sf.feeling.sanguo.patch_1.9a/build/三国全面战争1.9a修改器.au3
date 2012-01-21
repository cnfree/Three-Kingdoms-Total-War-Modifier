##########################################################################
# Copyright (c) 2011 cnfree.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
# cnfree - initial API and implementation
##########################################################################
#Region ;
#AutoIt3Wrapper_Compression=4
#AutoIt3Wrapper_UseUpx=n
#AutoIt3Wrapper_UseX64=n
#AutoIt3Wrapper_Res_Description=三国全面战争1.9a修改器
#AutoIt3Wrapper_Res_LegalCopyright=cnfree2000@hotmail.com
#AutoIt3Wrapper_Res_Fileversion=2.5.0.0
#AutoIt3Wrapper_Res_Field=CompanyName|三国全面战争百度贴吧
#AutoIt3Wrapper_Res_Field=ProductName|三国全面战争1.9a修改器
#AutoIt3Wrapper_Res_Field=ProductVersion|2.5
#AutoIt3Wrapper_Res_Language=2052
#EndRegion ;

$javahome = ""
$jrehome = ""
$vmargs = ""
$programargs = '"'&@AutoItExe&'"'
$debug = false

If $CmdLine[0] > 0 AND $CmdLine[1] == "/d" Then
	$debug = true
EndIf

If $debug == false Then
	If Not FileExists( @scriptdir&'\rt\bin\javaw.exe' ) AND Not FileExists( $javahome&'\bin\javaw.exe' ) AND Not FileExists( $jrehome&'\bin\javaw.exe' ) Then
		MsgBox(0+16,"Error", "The 三国全面战争1.9a修改器 executable launcher was unable to locate available Java Runtime Environment.")
		Exit
	EndIf
	If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
		RunWait(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist ".\bin\org.eclipse.jface.pack" (".\unpack200\unpack200.exe" ".\bin\org.eclipse.jface.pack" ".\bin\org.eclipse.jface.jar")& if exist ".\bin\org.eclipse.swt.win32.pack" (".\unpack200\unpack200.exe" ".\bin\org.eclipse.swt.win32.pack" ".\bin\org.eclipse.swt.win32.jar")& if exist ".\bin\org.eclipse.ui.forms.pack" (".\unpack200\unpack200.exe" ".\bin\org.eclipse.ui.forms.pack" ".\bin\org.eclipse.ui.forms.jar")& if exist ".\bin\org.sf.feeling.swt.win32.extension.pack" (".\unpack200\unpack200.exe" ".\bin\org.sf.feeling.swt.win32.extension.pack" ".\bin\org.sf.feeling.swt.win32.extension.jar")& if exist ".\bin\patch.pack" (".\unpack200\unpack200.exe" ".\bin\patch.pack" ".\bin\patch.jar")& if exist ".\bin\pinyin.pack" (".\unpack200\unpack200.exe" ".\bin\pinyin.pack" ".\bin\pinyin.jar")', @SystemDir, @sw_hide)
	EndIf
	If Not FileExists( $javahome&'\bin\javaw.exe' ) AND Not FileExists( $jrehome&'\bin\javaw.exe' ) Then
		If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
			RunWait(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist ".\rt\lib\alt-rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\alt-rt.pack" ".\rt\lib\alt-rt.jar")& if exist ".\rt\lib\charsets.pack" (".\unpack200\unpack200.exe" ".\rt\lib\charsets.pack" ".\rt\lib\charsets.jar")& if exist ".\rt\lib\ext\localedata.pack" (".\unpack200\unpack200.exe" ".\rt\lib\ext\localedata.pack" ".\rt\lib\ext\localedata.jar")& if exist ".\rt\lib\jce.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jce.pack" ".\rt\lib\jce.jar")& if exist ".\rt\lib\jsse.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jsse.pack" ".\rt\lib\jsse.jar")& if exist ".\rt\lib\resources.pack" (".\unpack200\unpack200.exe" ".\rt\lib\resources.pack" ".\rt\lib\resources.jar")& if exist ".\rt\lib\rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\rt.pack" ".\rt\lib\rt.jar")& if exist ".\rt\lib\security\local_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\local_policy.pack" ".\rt\lib\security\local_policy.jar")& if exist ".\rt\lib\security\US_export_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\US_export_policy.pack" ".\rt\lib\security\US_export_policy.jar")', @SystemDir, @sw_hide)
		EndIf
	EndIf

	Run(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist "'&$javahome&'\bin\javaw.exe" ("'&$javahome&'\bin\javaw.exe" -cp ".\bin\org.eclipse.jface.jar;.\bin\org.eclipse.swt.win32.jar;.\bin\org.eclipse.ui.forms.jar;.\bin\org.sf.feeling.swt.win32.extension.jar;.\bin\patch.jar;.\bin\pinyin.jar;" org.sf.feeling.sanguo.patch.Patch '&$programargs&' ) else if exist "'&$jrehome&'\bin\javaw.exe" ("'&$jrehome&'\bin\javaw.exe" -cp ".\bin\org.eclipse.jface.jar;.\bin\org.eclipse.swt.win32.jar;.\bin\org.eclipse.ui.forms.jar;.\bin\org.sf.feeling.swt.win32.extension.jar;.\bin\patch.jar;.\bin\pinyin.jar;" org.sf.feeling.sanguo.patch.Patch '&$programargs&' ) else (".\rt\bin\javaw.exe" -cp ".\bin\org.eclipse.jface.jar;.\bin\org.eclipse.swt.win32.jar;.\bin\org.eclipse.ui.forms.jar;.\bin\org.sf.feeling.swt.win32.extension.jar;.\bin\patch.jar;.\bin\pinyin.jar;" org.sf.feeling.sanguo.patch.Patch '&$programargs&' )', '', @SW_HIDE)
	If FileExists( $javahome&'\bin\javaw.exe' ) or FileExists( $jrehome&'\bin\javaw.exe' ) Then
		If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
			RunWait(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist ".\rt\lib\alt-rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\alt-rt.pack" ".\rt\lib\alt-rt.jar")& if exist ".\rt\lib\charsets.pack" (".\unpack200\unpack200.exe" ".\rt\lib\charsets.pack" ".\rt\lib\charsets.jar")& if exist ".\rt\lib\ext\localedata.pack" (".\unpack200\unpack200.exe" ".\rt\lib\ext\localedata.pack" ".\rt\lib\ext\localedata.jar")& if exist ".\rt\lib\jce.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jce.pack" ".\rt\lib\jce.jar")& if exist ".\rt\lib\jsse.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jsse.pack" ".\rt\lib\jsse.jar")& if exist ".\rt\lib\resources.pack" (".\unpack200\unpack200.exe" ".\rt\lib\resources.pack" ".\rt\lib\resources.jar")& if exist ".\rt\lib\rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\rt.pack" ".\rt\lib\rt.jar")& if exist ".\rt\lib\security\local_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\local_policy.pack" ".\rt\lib\security\local_policy.jar")& if exist ".\rt\lib\security\US_export_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\US_export_policy.pack" ".\rt\lib\security\US_export_policy.jar")', @SystemDir, @sw_hide)
		EndIf
	EndIf
Else
	If Not FileExists( @scriptdir&'\rt\bin\java.exe' ) AND Not FileExists( $javahome&'\bin\java.exe' ) AND Not FileExists( $jrehome&'\bin\java.exe' ) Then
		MsgBox(0+16,"Error", "The 三国全面战争1.9a修改器 executable launcher was unable to locate available Java Runtime Environment.")
		Exit
	EndIf
	If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
		RunWait(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist ".\bin\org.eclipse.jface.pack" (".\unpack200\unpack200.exe" ".\bin\org.eclipse.jface.pack" ".\bin\org.eclipse.jface.jar")& if exist ".\bin\org.eclipse.swt.win32.pack" (".\unpack200\unpack200.exe" ".\bin\org.eclipse.swt.win32.pack" ".\bin\org.eclipse.swt.win32.jar")& if exist ".\bin\org.eclipse.ui.forms.pack" (".\unpack200\unpack200.exe" ".\bin\org.eclipse.ui.forms.pack" ".\bin\org.eclipse.ui.forms.jar")& if exist ".\bin\org.sf.feeling.swt.win32.extension.pack" (".\unpack200\unpack200.exe" ".\bin\org.sf.feeling.swt.win32.extension.pack" ".\bin\org.sf.feeling.swt.win32.extension.jar")& if exist ".\bin\patch.pack" (".\unpack200\unpack200.exe" ".\bin\patch.pack" ".\bin\patch.jar")& if exist ".\bin\pinyin.pack" (".\unpack200\unpack200.exe" ".\bin\pinyin.pack" ".\bin\pinyin.jar")', @SystemDir, @sw_hide)
	EndIf
	If Not FileExists( $javahome&'\bin\java.exe' ) AND Not FileExists( $jrehome&'\bin\java.exe' ) Then
		If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
			RunWait(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist ".\rt\lib\alt-rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\alt-rt.pack" ".\rt\lib\alt-rt.jar")& if exist ".\rt\lib\charsets.pack" (".\unpack200\unpack200.exe" ".\rt\lib\charsets.pack" ".\rt\lib\charsets.jar")& if exist ".\rt\lib\ext\localedata.pack" (".\unpack200\unpack200.exe" ".\rt\lib\ext\localedata.pack" ".\rt\lib\ext\localedata.jar")& if exist ".\rt\lib\jce.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jce.pack" ".\rt\lib\jce.jar")& if exist ".\rt\lib\jsse.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jsse.pack" ".\rt\lib\jsse.jar")& if exist ".\rt\lib\resources.pack" (".\unpack200\unpack200.exe" ".\rt\lib\resources.pack" ".\rt\lib\resources.jar")& if exist ".\rt\lib\rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\rt.pack" ".\rt\lib\rt.jar")& if exist ".\rt\lib\security\local_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\local_policy.pack" ".\rt\lib\security\local_policy.jar")& if exist ".\rt\lib\security\US_export_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\US_export_policy.pack" ".\rt\lib\security\US_export_policy.jar")', @SystemDir, @sw_hide)
		EndIf
	EndIf
	Run(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist "'&$javahome&'\bin\java.exe" ("'&$javahome&'\bin\java.exe" -cp ".\bin\org.eclipse.jface.jar;.\bin\org.eclipse.swt.win32.jar;.\bin\org.eclipse.ui.forms.jar;.\bin\org.sf.feeling.swt.win32.extension.jar;.\bin\patch.jar;.\bin\pinyin.jar;" org.sf.feeling.sanguo.patch.Patch '&$programargs&'  & pause) else if exist "'&$jrehome&'\bin\java.exe" ("'&$jrehome&'\bin\java.exe" -cp ".\bin\org.eclipse.jface.jar;.\bin\org.eclipse.swt.win32.jar;.\bin\org.eclipse.ui.forms.jar;.\bin\org.sf.feeling.swt.win32.extension.jar;.\bin\patch.jar;.\bin\pinyin.jar;" org.sf.feeling.sanguo.patch.Patch '&$programargs&'  & pause) else (".\rt\bin\java.exe" -cp ".\bin\org.eclipse.jface.jar;.\bin\org.eclipse.swt.win32.jar;.\bin\org.eclipse.ui.forms.jar;.\bin\org.sf.feeling.swt.win32.extension.jar;.\bin\patch.jar;.\bin\pinyin.jar;" org.sf.feeling.sanguo.patch.Patch '&$programargs&'  & pause)')
	If FileExists( $javahome&'\bin\java.exe' ) or FileExists( $jrehome&'\bin\java.exe' ) Then
		If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
			RunWait(@ComSpec & ' /C cd /d "'&@scriptdir&'" & if exist ".\rt\lib\alt-rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\alt-rt.pack" ".\rt\lib\alt-rt.jar")& if exist ".\rt\lib\charsets.pack" (".\unpack200\unpack200.exe" ".\rt\lib\charsets.pack" ".\rt\lib\charsets.jar")& if exist ".\rt\lib\ext\localedata.pack" (".\unpack200\unpack200.exe" ".\rt\lib\ext\localedata.pack" ".\rt\lib\ext\localedata.jar")& if exist ".\rt\lib\jce.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jce.pack" ".\rt\lib\jce.jar")& if exist ".\rt\lib\jsse.pack" (".\unpack200\unpack200.exe" ".\rt\lib\jsse.pack" ".\rt\lib\jsse.jar")& if exist ".\rt\lib\resources.pack" (".\unpack200\unpack200.exe" ".\rt\lib\resources.pack" ".\rt\lib\resources.jar")& if exist ".\rt\lib\rt.pack" (".\unpack200\unpack200.exe" ".\rt\lib\rt.pack" ".\rt\lib\rt.jar")& if exist ".\rt\lib\security\local_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\local_policy.pack" ".\rt\lib\security\local_policy.jar")& if exist ".\rt\lib\security\US_export_policy.pack" (".\unpack200\unpack200.exe" ".\rt\lib\security\US_export_policy.pack" ".\rt\lib\security\US_export_policy.jar")', @SystemDir, @sw_hide)
		EndIf
	EndIf
EndIf

$unpack = false
If FileExists( @scriptdir&'\unpack200\unpack200.exe') Then
	$unpack = true
Else
	Exit
EndIf
$file = @scriptdir&'\bin\org.eclipse.jface.pack'
$jar = @scriptdir&'\bin\org.eclipse.jface.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\bin\org.eclipse.swt.win32.pack'
$jar = @scriptdir&'\bin\org.eclipse.swt.win32.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\bin\org.eclipse.ui.forms.pack'
$jar = @scriptdir&'\bin\org.eclipse.ui.forms.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\bin\org.sf.feeling.swt.win32.extension.pack'
$jar = @scriptdir&'\bin\org.sf.feeling.swt.win32.extension.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\bin\patch.pack'
$jar = @scriptdir&'\bin\patch.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\bin\pinyin.pack'
$jar = @scriptdir&'\bin\pinyin.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\alt-rt.pack'
$jar = @scriptdir&'\rt\lib\alt-rt.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\charsets.pack'
$jar = @scriptdir&'\rt\lib\charsets.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\ext\localedata.pack'
$jar = @scriptdir&'\rt\lib\ext\localedata.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\jce.pack'
$jar = @scriptdir&'\rt\lib\jce.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\jsse.pack'
$jar = @scriptdir&'\rt\lib\jsse.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\resources.pack'
$jar = @scriptdir&'\rt\lib\resources.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\rt.pack'
$jar = @scriptdir&'\rt\lib\rt.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\security\local_policy.pack'
$jar = @scriptdir&'\rt\lib\security\local_policy.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
$file = @scriptdir&'\rt\lib\security\US_export_policy.pack'
$jar = @scriptdir&'\rt\lib\security\US_export_policy.jar'
If FileExists( $jar ) Then
	If FileGetSize($jar) > 0 Then
		FileDelete($file)
	Else
		$unpack = false
	EndIf
Else
	$unpack = false
EndIf
If $unpack == true Then
	DirRemove(@scriptdir&'\unpack200', 1)
EndIf

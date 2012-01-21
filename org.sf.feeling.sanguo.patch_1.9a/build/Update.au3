#Region ;
#AutoIt3Wrapper_Compression=4
#AutoIt3Wrapper_UseUpx=n
#AutoIt3Wrapper_UseX64=n
#AutoIt3Wrapper_Res_Description=三国全面战争1.9a修改器升级程序
#AutoIt3Wrapper_Res_LegalCopyright=cnfree2000@hotmail.com
#AutoIt3Wrapper_Res_Fileversion=1.0.0.0
#AutoIt3Wrapper_Res_Field=CompanyName|三国全面战争百度贴吧
#AutoIt3Wrapper_Res_Field=ProductName|三国全面战争1.9a修改器升级程序
#AutoIt3Wrapper_Res_Field=ProductVersion|1.0
#AutoIt3Wrapper_Res_Language=2052
#EndRegion ;

$error = @scriptdir&"\..\.error"
$lock = @scriptdir&"\..\.lock"
$parent = @scriptdir&"\.."
$update = @scriptdir&"\patch"

if fileexists( $error ) then
	exit
endif

if not fileexists( $update ) then
	exit
endif

$flag = true
while $flag  
	if not fileexists( $lock )  then  
		$flag = false
	else 
		sleep( 1000)
	endif
wend

$flag = true
while $flag  
	$searchFile = $parent&"\*"
 	$search = filefindfirstfile( $searchFile) 
 	if $search <> -1 then
 		$endFind = false
 		while $endFind == false 
			$file = FileFindNextFile($search)    
			If @error Then                                        
				FileClose($search)  
				$endFind = true
			else
				if not ($file = "~bak") and not ($file = "update") then 
					$subfile = $parent&"\"&$file
					$subbakfile = $parent&"\~bak\"&$file
					$attrib = FileGetAttrib($subfile)
					if StringInStr($attrib, "D") then
						DirMove($subfile, $subbakfile, 1)    
					else 
						FileMove($subfile, $subbakfile, 1)
					endif              
				endif
			EndIf                          
		wend
	else
		exit                     
	endif
	
	$search = filefindfirstfile( $searchFile)
	$number = 0 
 	if $search <> -1 then
 		$endFind = false
 		while $endFind == false 
			$file = FileFindNextFile($search)    
			If @error Then                                        
				FileClose($search)  
				$endFind = true
			else
				if not ($file = "~bak") and not ($file = "update") then 
					$number = ($number + 1)
					FileClose($search)  
					$endFind = true             
				endif
			EndIf                          
		wend
	else
		exit                     
	endif
	
	if $number == 0 then  
		$flag = false
	else 
		sleep( 1000)
	endif
wend

$searchFile = $update&"\*"
$search = filefindfirstfile( $searchFile) 
if $search <> -1 then
 	$endFind = false
 	while $endFind == false 
		$file = FileFindNextFile($search)    
		If @error Then                                        
			FileClose($search)  
			$endFind = true
		else
			$updatefile = $update&"\"&$file
			$patchfile = $parent&"\"&$file
			$attrib = FileGetAttrib($updatefile)
			if StringInStr($attrib, "D") then
				DirMove($updatefile, $patchfile, 1)    
			else 
				FileMove($updatefile, $patchfile, 1)
			endif
		endIf                          
	wend
else
	exit                     
endif

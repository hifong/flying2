mkdir “{$compile_folder}”
rem cd “{$compile_folder}”
C:\Tools\svn\svn.exe copy {$version_code_base} {$tag_code_base} -m "{$version} released"
C:\Tools\svn\svn.exe checkout {$tag_code_base} {$compile_folder} --username WangHaiFeng
rem C:\Tools\svn\svn.exe checkout {$version_code_base} {$compile_folder} --username WangHaiFeng
mvn clean package -f {$compile_folder}\pom.xml
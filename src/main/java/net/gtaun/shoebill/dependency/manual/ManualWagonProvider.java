/**
 * Copyright (C) 2012 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill.dependency.manual;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.apache.maven.wagon.providers.ftp.FtpWagon;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.eclipse.aether.connector.wagon.WagonProvider;

/**
 * 
 * 
 * @author MK124
 */
public class ManualWagonProvider implements WagonProvider
{
	@Override
	public Wagon lookup(String roleHint) throws Exception
	{
		if ("http".equals(roleHint)) return new HttpWagon();
		if ("https".equals(roleHint)) return new HttpWagon();
		if ("ftp".equals(roleHint)) return new FtpWagon();
		if ("file".equals(roleHint)) return new FileWagon();
		
		return null;
	}
	
	@Override
	public void release(Wagon wagon)
	{
		
	}
}

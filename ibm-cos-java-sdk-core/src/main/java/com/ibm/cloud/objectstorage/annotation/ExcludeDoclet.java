/*
* Copyright 2018 IBM Corp. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
* specific language governing permissions and limitations under the License.
*/
package com.ibm.cloud.objectstorage.annotation;

import com.sun.javadoc.*;
import com.sun.tools.doclets.standard.Standard;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create an annotation which can exclude methods & variables
 * from java doc generation.
 * This class is excluded from the main maven build as it relies on Jars which are not 
 * installed by default Java 1.9 upwards. Is is only compiled when using the docgen profile
 * and only required by the this profile to generate docs which exclude non supported features.
 *
 */
@SuppressWarnings("restriction")
public class ExcludeDoclet extends com.sun.tools.doclets.standard.Standard {	

	public static boolean start(RootDoc rootIBMDoc)  {

		return Standard.start((RootDoc) process(rootIBMDoc, RootDoc.class));
	}

	private static Object process(Object obj, Class<?> expect) {

		try{
			if (obj == null)
				return null;
			Class<?> cls = obj.getClass();
			if (cls.getName().startsWith("com.sun.")) {
				return Proxy.newProxyInstance(cls.getClassLoader(),
						cls.getInterfaces(), new ExcludeIBMUnSupported(obj));
			} else if (obj instanceof Object[]) {
				Class<?> componentType = expect.getComponentType();
				Object[] array = (Object[]) obj;
				List<Object> list = new ArrayList<Object>(array.length);
				for (int i = 0; i < array.length; i++) {
					Object entry = array[i];
					if ((entry instanceof Doc) && exclude((Doc) entry))
						continue;
					list.add(process(entry, componentType));
				}
				return list.toArray((Object[]) Array.newInstance(componentType,
						list.size()));
			} else {
				return obj;
			}
		} catch (Exception e){
			return null;
		}
	}

	private static boolean exclude(Doc doc) {
		if (doc instanceof ProgramElementDoc) {
			if (((ProgramElementDoc) doc).containingPackage().tags("exclude").length > 0) {

				return true;
			}
		}
		boolean result = doc.tags("exclude").length > 0;

		return result;
	}

	private static class ExcludeIBMUnSupported implements InvocationHandler {
		private Object target;

		public ExcludeIBMUnSupported(Object target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			if (args != null) {
				String methodName = method.getName();
				if (methodName.equals("compareTo")
						|| methodName.equals("equals")
						|| methodName.equals("overrides")
						|| methodName.equals("subclassOf")) {
					args[0] = unwrap(args[0]);
				}
			}
			try {
				return process(method.invoke(target, args),
						method.getReturnType());
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		private Object unwrap(Object proxy) {

			if (proxy instanceof Proxy)
				return ((ExcludeIBMUnSupported) Proxy.getInvocationHandler(proxy)).target;
			return proxy;
		}
	}
}

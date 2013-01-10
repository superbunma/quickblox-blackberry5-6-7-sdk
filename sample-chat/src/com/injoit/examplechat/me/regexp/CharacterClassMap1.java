/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.injoit.examplechat.me.regexp;

//#ifdef RE_UNICODE
//# /**
//#  *
//#  * @author Nikolay Neizvesny
//#  */
//# class CharacterClassMap1 {
//#     static final byte[] CHAR_CLASSES_0 = {
//#         15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
//#         15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 12, 23, 23, 23,
//#         25, 23, 23, 23, 20, 21, 23, 24, 23, 19, 23, 23, 9, 9, 9, 9, 9, 9, 9, 9, 9,
//#         9, 23, 23, 24, 24, 24, 23, 23, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//#         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 20, 23, 21, 26, 22, 26, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 20, 24, 21,
//#         24, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
//#         15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 12, 23,
//#         25, 25, 25, 25, 27, 27, 26, 27, 2, 28, 24, 16, 27, 26, 27, 24, 11, 11, 26,
//#         2, 27, 23, 26, 11, 2, 29, 11, 11, 11, 23, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//#         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 24, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 24, 2, 2, 2,
//#         2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         1, 1, 2, 1, 2, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1,
//#         1, 2, 1, 1, 2, 1, 1, 1, 2, 2, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 1,
//#         2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2, 2, 5, 1, 2, 2, 2, 5, 5, 5,
//#         5, 1, 3, 2, 1, 3, 2, 1, 3, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 3, 2, 1,
//#         2, 1, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 1, 1, 2,
//#         2, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 5, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
//#         4, 4, 4, 4, 4, 26, 26, 26, 26, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 26, 26,
//#         26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 4, 4, 4, 4, 4, 26, 26, 26,
//#         26, 26, 26, 26, 26, 26, 4, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
//#         26, 26, 26, 26, 26, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 26,
//#         26, 0, 0, 0, 0, 4, 2, 2, 2, 23, 0, 0, 0, 0, 0, 26, 26, 1, 23, 1, 1, 1, 0,
//#         1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
//#         1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 1, 1, 1, 2, 2,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         2, 2, 2, 2, 1, 2, 24, 1, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//#         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//#         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 27, 6,
//#         6, 6, 6, 0, 7, 7, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#         1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
//#         2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
//#     };
//#     static final byte[] CHAR_CLASSES_1 = {
//#         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//#         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 4, 23, 23, 23, 23, 23, 23, 0,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//#         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 23, 19, 0, 0, 0, 0, 0, 0, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 23, 6, 23, 6, 6,
//#         23, 6, 6, 23, 6, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 5, 5, 5,
//#         23, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16, 16, 16, 0, 0, 0, 0, 0, 0,
//#         0, 25, 23, 23, 27, 27, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 23, 0, 0, 23, 23,
//#         0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 0, 0, 0, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
//#         23, 23, 23, 23, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 23, 5, 6, 6, 6, 6, 6, 6, 6, 16, 7, 6, 6, 6, 6, 6,
//#         6, 4, 4, 6, 6, 27, 6, 6, 6, 6, 5, 5, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 5, 5,
//#         5, 27, 27, 5, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 0,
//#         16, 5, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0,
//#         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//#         0, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 4, 4, 27, 23, 23, 23, 4,
//#     };
//#     static final byte[] CHAR_CLASSES_2 = {
//#         6, 6, 8, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 0, 0, 6, 5, 8, 8, 8, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8,
//#         8, 6, 0, 0, 5, 6, 6, 6, 6, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6,
//#         23, 23, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
//#         5, 5, 5, 5, 0, 6, 8, 8, 0, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 5, 5, 0, 0, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5,
//#         5, 5, 5, 0, 5, 0, 0, 0, 5, 5, 5, 5, 0, 0, 6, 5, 8, 8, 8, 6, 6, 6, 6, 0, 0,
//#         8, 8, 0, 0, 8, 8, 6, 5, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 5, 5, 0, 5,
//#         5, 5, 6, 6, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 5, 5, 25, 25, 11, 11, 11,
//#         11, 11, 11, 27, 0, 0, 0, 0, 0, 0, 6, 6, 8, 0, 5, 5, 5, 5, 5, 5, 0, 0, 0,
//#         0, 5, 5, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 0, 5, 5, 0, 5, 5, 0, 0, 6, 0, 8, 8,
//#         8, 6, 6, 0, 0, 0, 0, 6, 6, 0, 0, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//#         5, 5, 5, 5, 0, 5, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 6,
//#         5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 8, 0, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 0, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 0, 5, 5, 5, 5, 5, 0, 0, 6,
//#         5, 8, 8, 8, 6, 6, 6, 6, 6, 0, 6, 6, 8, 0, 8, 8, 6, 0, 0, 5, 0, 0, 0, 0, 0,
//#         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 6, 6, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9,
//#         9, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 8, 8, 0, 5, 5,
//#         5, 5, 5, 5, 5, 5, 0, 0, 5, 5, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 0, 5, 5, 5, 5,
//#         5, 0, 0, 6, 5, 8, 6, 8, 6, 6, 6, 0, 0, 0, 8, 8, 0, 0, 8, 8, 6, 0, 0, 0, 0,
//#         0, 0, 0, 0, 6, 8, 0, 0, 0, 0, 5, 5, 0, 5, 5, 5, 0, 0, 0, 0, 9, 9, 9, 9, 9,
//#         9, 9, 9, 9, 9, 27, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6,
//#         5, 0, 5, 5, 5, 5, 5, 5, 0, 0, 0, 5, 5, 5, 0, 5, 5, 5, 5, 0, 0, 0, 5, 5, 0,
//#         5, 0, 5, 5, 0, 0, 0, 5, 5, 0, 0, 0, 5, 5, 5, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 0, 0, 0, 0, 8, 8, 6, 8, 8, 0, 0, 0, 8, 8, 8, 0, 8, 8, 8, 6,
//#         0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9,
//#         9, 9, 9, 9, 9, 9, 9, 9, 9, 11, 11, 11, 27, 27, 27, 27, 27, 27, 25, 27, 0,
//#         0, 0, 0, 0, 0, 8, 8, 8, 0, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 0, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 0, 0, 0, 0, 6, 6, 6, 8, 8, 8, 8, 0, 6,
//#         6, 6, 0, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//#         5, 5, 0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//#         0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 0, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 0,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 0, 0, 6, 5, 8, 6, 8, 8, 8, 8,
//#         8, 0, 6, 8, 8, 0, 8, 8, 6, 6, 0, 0, 0, 0, 0, 0, 0, 8, 8, 0, 0, 0, 0, 0, 0,
//#         0, 5, 0, 5, 5, 6, 6, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 27, 27, 0, 0,
//#         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 0, 5, 5, 5, 5, 5, 5, 5, 5, 0,
//#         5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 8, 8,
//#         8, 6, 6, 6, 0, 0, 8, 8, 8, 0, 8, 8, 8, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0,
//#         0, 0, 0, 0, 0, 0, 0, 5, 5, 0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 0,
//#         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 0, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0,
//#         5, 0, 0, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 6, 0, 0, 0, 0, 8, 8, 8, 6, 6, 6, 0,
//#         6, 0, 8, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//#         0, 0, 0, 8, 8, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 6, 6, 6, 6, 6,
//#         6, 6, 0, 0, 0, 0, 25, 5, 5, 5, 5, 5, 5, 4, 6, 6, 6, 6, 6, 6, 6, 6, 23, 9,
//#         9, 9, 9, 9, 9, 9, 9, 9, 9, 23, 23,
//#     };
//#     static final byte[] CHAR_CLASSES_3 = {
//#         5, 5, 0, 5, 0, 0, 5, 5, 0, 5, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 0, 5,
//#         5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 0, 5, 0, 5, 0, 0, 5, 5, 0, 5, 5, 5, 5, 6, 5,
//#         5, 6, 6, 6, 6, 6, 6, 0, 6, 6, 5, 0, 0, 5, 5, 5, 5, 5, 0, 4, 0, 6, 6, 6, 6,
//#         6, 6, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 0, 5, 5,
//#     };
//#     static final byte[] CHAR_CLASSES_4 = {
//#         5, 27, 27, 27, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
//#         27, 27, 27, 27, 27, 6, 6, 27, 27, 27, 27, 27, 27, 9, 9, 9, 9, 9, 9, 9, 9,
//#         9, 9, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 27, 6, 27, 6, 27, 6, 20, 21,
//#         20, 21, 8, 8, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0,
//#         0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 6, 6, 6, 6, 6,
//#         23, 6, 6, 5, 5, 5, 5, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
//#         6, 6, 6, 6, 6, 6, 6, 0, 27, 27, 27, 27, 27, 27, 27, 27, 6, 27, 27, 27, 27,
//#         27, 27, 0, 0, 27, 23, 23,
//#     };
//#     static final byte[] CHAR_CLASSES_5 = {
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
//#         5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 0, 5, 5, 0, 8, 6, 6, 6, 6, 8,
//#         6, 0, 0, 0, 6, 6, 8, 6, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
//#         23, 23, 23, 23, 23, 23, 5, 5, 5, 5, 5, 5, 8, 8, 6, 6,
//#     };
//# }
//#endif

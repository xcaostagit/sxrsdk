/* Copyright 2015 Samsung Electronics Co., LTD
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

/***************************************************************************
 * Printing C++ stacktrace when native crash happens. Using tag sxrf
 ***************************************************************************/

#ifndef SXR_CPP_STACK_TRACE_H_
#define SXR_CPP_STACK_TRACE_H_

void printStackTrace(unsigned int max_frames = 10);

#endif // SXR_CPP_STACK_TRACE_H_

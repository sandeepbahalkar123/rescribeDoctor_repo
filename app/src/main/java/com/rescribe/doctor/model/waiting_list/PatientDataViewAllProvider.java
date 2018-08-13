/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rescribe.doctor.model.waiting_list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PatientDataViewAllProvider extends AbstractDataProvider {
    private List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public PatientDataViewAllProvider(ArrayList<ViewAll> viewAlls) {
        mData = new LinkedList<>();
        for (int i = 0; i < viewAlls.size(); i++) {
            final long id = mData.size();
            final int viewType = 0;
            mData.add(new ConcreteData(id, viewType, viewAlls.get(i)));
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
//            throw new IndexOutOfBoundsException("index = " + index);
            return mData.get(getCount() - 1);
        } else {
            return mData.get(index);
        }
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(mData, toPosition, fromPosition);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        if (mData.size() > position) {
            mLastRemovedData = mData.remove(position);
            mLastRemovedPosition = position;
        }
    }

    @Override
    public void addViewAllItem(ViewAll viewAll) {
        mData.add(new ConcreteData(mData.size(), 0, viewAll));
    }

    @Override
    public List<ConcreteData> getViewAllData() {
        return mData;
    }

    @Override
    public List<PatientDataActiveWaitingListProvider.ConcreteActiveData> getActiveData() {
        return null;
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        private final ViewAll mViewAll;
        private final int mViewType;
        private boolean mPinned;

        ConcreteData(long id, int viewType, ViewAll viewAll) {
            mId = id;
            mViewType = viewType;
            mViewAll = viewAll;
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public ViewAll getViewAll() {
            return mViewAll;
        }

        @Override
        public Active getActiveAll() {
            return null;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public String toString() {
            return mViewAll.getPatientName();
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }
    }
}

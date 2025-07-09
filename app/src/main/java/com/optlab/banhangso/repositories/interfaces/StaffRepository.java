package com.optlab.banhangso.repositories.interfaces;

import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Staff;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface StaffRepository extends BaseRepository {

  Flowable<PagingData<Staff>> getStaffs();

  Flowable<PagingData<Staff>> searchStaffs(String query);

  Single<Result<Staff>> getStaff(String staffId);

  Single<Result<Staff>> updateStaff(Staff staff);

  Single<Result<Void>> createStaff(Staff staff);

  Single<Result<Void>> deleteStaff(String staffId);
}

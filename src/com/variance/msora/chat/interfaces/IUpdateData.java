package com.variance.msora.chat.interfaces;

import com.variance.msora.chat.types.FriendInfo;


public interface IUpdateData {
	public void updateData(FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}

package com.david4.test.spring;
public class AC_SAM{
		String sam_id,sam_type,remark;

		public String getSam_id() {
			return sam_id;
		}

		public void setSam_id(String sam_id) {
			this.sam_id = sam_id;
		}

		public String getSam_type() {
			return sam_type;
		}

		public void setSam_type(String sam_type) {
			this.sam_type = sam_type;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		@Override
		public String toString() {
			return "AC_SAM [sam_id=" + sam_id + ", sam_type=" + sam_type
					+ ", remark=" + remark + "]";
		}
	}
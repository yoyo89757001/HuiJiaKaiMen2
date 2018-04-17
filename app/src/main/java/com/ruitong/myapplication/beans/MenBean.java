package com.ruitong.myapplication.beans;

/**
 * Created by Administrator on 2018/4/16.
 */

public class MenBean {


    /**
     * person : {"feature_id":0,"confidence":74.861885,"tag":{"subject_type":0,"description":"","start_time":0,"birthday":null,"id":106,"remark":"","name":"军","title":"","job_number":"","entry_date":null,"end_time":0,"department":"","avatar":"/static/upload/photo/2018-04-02/bb6d83926adfba5f71f635e5b2b4412be6f5dd1a.jpg"},"id":"106"}
     * can_door_open : true
     * error : 0
     */

    private PersonBean person;
    private boolean can_door_open;
    private int error;

    public PersonBean getPerson() {
        return person;
    }

    public void setPerson(PersonBean person) {
        this.person = person;
    }

    public boolean isCan_door_open() {
        return can_door_open;
    }

    public void setCan_door_open(boolean can_door_open) {
        this.can_door_open = can_door_open;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public static class PersonBean {
        /**
         * feature_id : 0
         * confidence : 74.861885
         * tag : {"subject_type":0,"description":"","start_time":0,"birthday":null,"id":106,"remark":"","name":"军","title":"","job_number":"","entry_date":null,"end_time":0,"department":"","avatar":"/static/upload/photo/2018-04-02/bb6d83926adfba5f71f635e5b2b4412be6f5dd1a.jpg"}
         * id : 106
         */

        private int feature_id;
        private double confidence;
        private TagBean tag;
        private String id;

        public int getFeature_id() {
            return feature_id;
        }

        public void setFeature_id(int feature_id) {
            this.feature_id = feature_id;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public TagBean getTag() {
            return tag;
        }

        public void setTag(TagBean tag) {
            this.tag = tag;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class TagBean {
            /**
             * subject_type : 0
             * description :
             * start_time : 0
             * birthday : null
             * id : 106
             * remark :
             * name : 军
             * title :
             * job_number :
             * entry_date : null
             * end_time : 0
             * department :
             * avatar : /static/upload/photo/2018-04-02/bb6d83926adfba5f71f635e5b2b4412be6f5dd1a.jpg
             */

            private int subject_type;
            private String description;
            private int start_time;
            private Object birthday;
            private int id;
            private String remark;
            private String name;
            private String title;
            private String job_number;
            private Object entry_date;
            private int end_time;
            private String department;
            private String avatar;

            public int getSubject_type() {
                return subject_type;
            }

            public void setSubject_type(int subject_type) {
                this.subject_type = subject_type;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public int getStart_time() {
                return start_time;
            }

            public void setStart_time(int start_time) {
                this.start_time = start_time;
            }

            public Object getBirthday() {
                return birthday;
            }

            public void setBirthday(Object birthday) {
                this.birthday = birthday;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getJob_number() {
                return job_number;
            }

            public void setJob_number(String job_number) {
                this.job_number = job_number;
            }

            public Object getEntry_date() {
                return entry_date;
            }

            public void setEntry_date(Object entry_date) {
                this.entry_date = entry_date;
            }

            public int getEnd_time() {
                return end_time;
            }

            public void setEnd_time(int end_time) {
                this.end_time = end_time;
            }

            public String getDepartment() {
                return department;
            }

            public void setDepartment(String department) {
                this.department = department;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}

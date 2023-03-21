```sql
use adonis;

create table user(
id varchar(20) primary key,
nickname varchar(20) NOT NULL,
password varchar(20) NOT NULL
);

create table dialogue(
senderId varchar(20),
receiverId varchar(20),
content varchar(20),
lastedTime double,
occuringTime timestamp not null
);

create table friend(
subject varchar(20),
object varchar(20)
);

alter table dialogue
add constraint fk_dialogue_senderId
foreign key(senderId)
references user(id);

alter table dialogue
add constraint fk_dialogue_receiverId
foreign key(receiverId)
references user(id);

alter table friend
add constraint fk_friend_subject
foreign key(subject)
references user(id);

alter table friend
add constraint fk_friend_object
foreign key(object)
references user(id);
```
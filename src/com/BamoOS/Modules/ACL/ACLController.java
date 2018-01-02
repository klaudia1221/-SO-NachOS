package com.BamoOS.Modules.ACL;


import com.BamoOS.Modules.ACL.Interfaces.IACLController;
import com.BamoOS.Modules.ACL.Interfaces.IUserController;
import com.BamoOS.Modules.FileSystem.FileBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACLController implements IACLController {
    private Map<Integer, List<ACE>> DACLs;
    private IUserController userController;

    public ACLController(IUserController userController){
        this.userController = userController;
        DACLs = new HashMap<>();
    }

    public void ShowACEForFfile(FileBase file){
        if(DACLs.containsKey(file.getId())){
            for(ACE ace : DACLs.get(file.getId())){
                if(ace.isGroupAce()){
                    System.out.println(ace.getGroup().getName());
                }else{
                    System.out.println(ace.getUser().getName());
                }
            }
        }
    }

    @Override
    public void addAceForUser(User user, Mask mask, FileBase file) {
        boolean aceForUserExist = false;
        if(DACLs.containsKey(file.getId())){
            for(ACE ace : DACLs.get(file.getId())){
                if(!ace.isGroupAce()){
                    if(ace.getUser().getName().equals(user.getName())){
                        ace.setMask(mask);
                        aceForUserExist = true;
                        break;
                    }
                }
            }
            if(!aceForUserExist){
                ACE ace = new ACE(user);
                ace.setMask(mask);
                DACLs.get(file.getId()).add(ace);
            }
        }else{
            ACE ace = new ACE(user);
            ace.setMask(mask);
            List<ACE> listACE = new ArrayList<>();
            listACE.add(ace);
            DACLs.put(file.getId(), listACE);
        }
    }

    @Override
    public void addAceForGroup(Group group, Mask mask, FileBase file) {
        boolean aceForGroupExist = false;
        if(DACLs.containsKey(file.getId())){
            for(ACE ace : DACLs.get(file.getId())){
                if(ace.isGroupAce()){
                    if(ace.getGroup().getName().equals(group.getName())){
                        ace.setMask(mask);
                        aceForGroupExist = true;
                        break;
                    }
                }
            }
            if(!aceForGroupExist){
                ACE ace = new ACE(group);
                ace.setMask(mask);
                DACLs.get(file.getId()).add(ace);
            }
        }else{
            ACE ace = new ACE(group);
            ace.setMask(mask);
            List<ACE> listACE = new ArrayList<>();
            listACE.add(ace);
            DACLs.put(file.getId(), listACE);
        }
    }
    private List<ACE> getFileACESForUser(User user, int fileID){
        List<Group> userGroups;
        List<ACE> tempACEs = new ArrayList<>();
        try {
            userGroups = userController.getUserGroups(user.getName());
            if(DACLs.containsKey(fileID)){
                for(ACE ace : DACLs.get(fileID)){
                    if(!ace.isGroupAce() && ace.getUser().getName().equals(user.getName())){
                        tempACEs.add(ace);
                    }
                    if(ace.isGroupAce()){
                        for(Group group : userGroups){
                            if(ace.getGroup().getName().equals(group.getName())){
                                tempACEs.add(ace);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempACEs;
    }

    @Override
    public boolean hasUserPremissionToOperation(FileBase file, User user, OperationType type) {
        if(user.getName().equals(file.getOwner().getName())){
            return true;
        }
        boolean hasPremission = false;
        List<ACE> fileACESAssociatedWithUser = getFileACESForUser(user,file.getId());
        switch (type){
            case READ:
                for(ACE ace : fileACESAssociatedWithUser){
                    if(ace.getMask().isWrite() || ace.getMask().isRead()){
                        hasPremission = true;
                        break;
                    }
                }
                break;
            case MODIFY:
                for(ACE ace : fileACESAssociatedWithUser){
                    if(ace.getMask().isWrite()){
                        hasPremission = true;
                        break;
                    }
                }
                break;
            case EXECUTE:
                for(ACE ace : fileACESAssociatedWithUser){
                    if(ace.getMask().isExecute()){
                        hasPremission = true;
                        break;
                    }
                }
                break;
            default:
                hasPremission = false;
                break;
        }
        return hasPremission;
    }

    @Override
    public void setPremissionToDefaultGroup(FileBase file) throws Exception {
        Mask mask = new Mask(true,false,false);
        boolean aceSet = false;
        if(DACLs.containsKey(file.getId())){
            for(ACE ace : DACLs.get(file.getId())){
                if(ace.isGroupAce()){
                    if(ace.getGroup().getName().equals("default")){
                        ace.setMask(mask);
                        aceSet = true;
                        break;
                    }
                }
            }
        }else{
            ACE ace = new ACE(userController.getGroup("default"));
            ace.setMask(mask);
            List<ACE> ACEs = new ArrayList<>();
            ACEs.add(ace);
            DACLs.put(file.getId(),ACEs);
            aceSet = true;
        }
        if(!aceSet){
            ACE ace = new ACE(userController.getGroup("default"));
            ace.setMask(mask);
            DACLs.get(file.getId()).add(ace);
        }
    }

    @Override
    public void setPremissionToAdminGroup(FileBase file) throws Exception {
        Mask mask = new Mask(true,true,true);
        boolean aceSet = false;
        if(DACLs.containsKey(file.getId())){
            for(ACE ace : DACLs.get(file.getId())){
                if(ace.isGroupAce()){
                    if(ace.getGroup().getName().equals("admin")){
                        ace.setMask(mask);
                        aceSet = true;
                        break;
                    }
                }
            }
        }else{
            ACE ace = new ACE(userController.getGroup("admin"));
            ace.setMask(mask);
            List<ACE> ACEs = new ArrayList<>();
            ACEs.add(ace);
            DACLs.put(file.getId(),ACEs);
            aceSet = true;
        }
        if(!aceSet){
            ACE ace = new ACE(userController.getGroup("admin"));
            ace.setMask(mask);
            DACLs.get(file.getId()).add(ace);
        }
    }

    public void setDefaultPremissionToFile(FileBase file) throws Exception {
        setPremissionToAdminGroup(file);
        setPremissionToDefaultGroup(file);
    }
}

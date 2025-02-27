package org.example.overview.members.service;

import org.example.overview.members.dao.MemberDAO;
import org.example.overview.members.dto.MemberDTO;
import org.example.overview.members.dto.Password;
import org.example.overview.members.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService implements IMemberService {

    private MemberDAO memberDAO;

    @Autowired
    public MemberService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> findByUserIdOrEmail(String q) {
        if (q == null) return null;

        List<Member> memberList = memberDAO.searchMember(q);
        return memberList.stream().map(m -> m.toDTO()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean autoLogin(String autoLogin, String cookieId) {
        if (autoLogin == null || cookieId == null) return false;

        if (autoLogin.equals("true")) {
            if (getByUserId(cookieId) != null) {
                MemberDTO memberDTO = login(cookieId);
                return memberDTO != null;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO login(String uId) {
        MemberDTO memberDTO = new MemberDTO(uId);
        if (memberDTO == null) return null;

        Member member = memberDAO.selectMember(memberDTO.getuId());
        return member.toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO login(String uId, Password uPw) {
        MemberDTO memberDTO = new MemberDTO(uId, uPw);
        if (memberDTO == null || memberDTO.getuPwStr() == null) return null;

        Member member = memberDAO.selectMember(memberDTO.getuId());
        if (member == null || member.getuPw() == null) return null;
        if (member.getuPw().equals(memberDTO.getuPwStr())) {
            return member.toDTO();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean signup(String uId, Password uPw, String uEmail) {
        MemberDTO memberDTO = new MemberDTO(uId, uPw, uEmail);
        if (memberDTO == null || memberDTO.getuPwStr() == null) return false;

        int res = memberDAO.insertMember(memberDTO.toEntity());
        return res > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getByUserId(String uId) {
        if (uId == null) return null;

        Member member = memberDAO.selectMember(uId);
        if (member == null) return null;

        return member.toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllUsers() {
        List<Member> memberList = memberDAO.selectMembers();
        return memberList.stream().map(m -> m.toDTO()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateUserPassword(String uId, Password uPw, Password uNewPw) {
        if (uId == null || uPw == null || uNewPw == null) return false;

        Member member = memberDAO.selectMember(uId);
        if (member == null || member.getuPw() == null) return false;
        if (!member.getuPw().equals(uPw.getuPw())) return false;
        if (member.getuPw().equals(uNewPw.getuPw())) return false; // DB PWD == New PWD

        int res = memberDAO.updateMemberPassword(uId, uNewPw.getuPw());
        return res > 0;
    }

    @Override
    @Transactional
    public boolean updateUserEmail(String uId, String uEmail) {
        if (uId == null || uEmail == null) return false;

        Member member = memberDAO.selectMember(uId);
        if (member == null) return false;
        if (member.getuEmail().equals(uEmail)) return false; // DB Email == New Email

        int res = memberDAO.updateMemberEmail(uId, uEmail);
        return res > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPassword(String uId, Password uPw) {
        if (uId == null || uPw == null) return false;

        Member member = memberDAO.selectMember(uId);
        if (member == null) return false;
        if (!member.getuPw().equals(uPw.getuPw())) return false;

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkNewPassword(String uId, Password uNewPw) {
        if (uId == null || uNewPw == null) return false;

        Member member = memberDAO.selectMember(uId);
        if (member == null) return false;
        if (member.getuPw().equals(uNewPw.getuPw())) return false;


        return true;
    }



    @Override
    @Transactional
    public boolean removeByUserId(String uId, Password uPw) {
        if (uId == null || uPw == null) return false;

        Member member = memberDAO.selectMember(uId);
        System.out.println(member);
        if (member == null || member.getuPw() == null) return false;
        if (!member.getuPw().equals(uPw.getuPw())) return false;

        int res = memberDAO.deleteMember(uId);
        return res > 0;
    }

    @Override
    @Transactional
    public boolean removeUsers() {
        int res = memberDAO.deleteMembers();
        return res > 0;
    }

}